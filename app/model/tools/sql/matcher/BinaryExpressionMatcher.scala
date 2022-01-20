package model.tools.sql.matcher

import model.matching._
import model.points._
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.schema.Column

final case class BinaryExpressionMatch(matchType: MatchType, userArg: BinaryExpression, sampleArg: BinaryExpression) extends Match[BinaryExpression] {

  override def points: Points = matchType match {
    case MatchType.SUCCESSFUL_MATCH   => singlePoint
    case MatchType.UNSUCCESSFUL_MATCH => singleHalfPoint
    case _                            => zeroPoints
  }

  override val maxPoints: Points = singlePoint

}

final case class BinaryExpressionMatcher(
  userTAliases: Map[String, String],
  sampleTAliases: Map[String, String]
) extends Matcher[BinaryExpression, BinaryExpressionMatch] {

  private def getColToCompare(expression: BinaryExpression): Option[Column] = expression.getLeftExpression match {
    case left: Column =>
      Some(expression.getRightExpression match {
        case right: Column => if (left.toString < right.toString) left else right
        case _             => left
      })
    case _ =>
      expression.getRightExpression match {
        case right: Column => Some(right)
        case _             => None
      }
  }

  private def maybeTableAlias(col: Column): Option[String] = Option(col.getTable).map(_.getName)

  override protected def canMatch(binEx1: BinaryExpression, binEx2: BinaryExpression): Boolean = (for {
    colComp1 <- getColToCompare(binEx1)
    colComp2 <- getColToCompare(binEx2)
  } yield {
    val table1 = maybeTableAlias(colComp1).map(a => userTAliases.getOrElse(a, a)).getOrElse("")
    val table2 = maybeTableAlias(colComp2).map(a => sampleTAliases.getOrElse(a, a)).getOrElse("")

    colComp1.getColumnName == colComp2.getColumnName && table1 == table2
  }).getOrElse(???)

  override protected def instantiateMatch(ua: BinaryExpression, sa: BinaryExpression): BinaryExpressionMatch = {
    val (a1Left, a1Right) = (ua.getLeftExpression.toString, ua.getRightExpression.toString)
    val (a2Left, a2Right) = (sa.getLeftExpression.toString, sa.getRightExpression.toString)

    val parallelEqual = (a1Left == a2Left) && (a1Right == a2Right)
    val crossedEqual  = (a1Left == a2Right) && (a1Right == a2Left)

    val matchType: MatchType = if (parallelEqual || crossedEqual) {
      MatchType.SUCCESSFUL_MATCH
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }

    BinaryExpressionMatch(matchType, ua, sa)
  }
}
