package model.tools.sql.matcher

import model.core.matching._
import model.points._
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.schema.Column

final case class BinaryExpressionMatch(
  matchType: MatchType,
  userArg: Option[BinaryExpression],
  sampleArg: Option[BinaryExpression]
) extends Match[BinaryExpression] {

  override def points: Points = matchType match {
    case MatchType.SUCCESSFUL_MATCH   => singlePoint
    case MatchType.UNSUCCESSFUL_MATCH => singleHalfPoint
    case _                            => zeroPoints
  }

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singlePoint
  }

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

  override protected def canMatch(binEx1: BinaryExpression, binEx2: BinaryExpression): Boolean = {

    def maybeTableAlias(col: Column): Option[String] = Option(col.getTable).map(_.getName)

    getColToCompare(binEx1) match {
      case None => ???
      case Some(colComp1) =>
        getColToCompare(binEx2) match {
          case None => ???
          case Some(colComp2) =>
            val table1 = maybeTableAlias(colComp1).map(a => userTAliases.getOrElse(a, a)).getOrElse("")
            val table2 = maybeTableAlias(colComp2).map(a => sampleTAliases.getOrElse(a, a)).getOrElse("")

            colComp1.getColumnName == colComp2.getColumnName && table1 == table2
        }
    }
  }

  override protected def instantiateOnlySampleMatch(sa: BinaryExpression): BinaryExpressionMatch =
    BinaryExpressionMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: BinaryExpression): BinaryExpressionMatch =
    BinaryExpressionMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: BinaryExpression, sa: BinaryExpression): BinaryExpressionMatch = {
    val (a1Left, a1Right) = (ua.getLeftExpression.toString, ua.getRightExpression.toString)
    val (a2Left, a2Right) = (sa.getLeftExpression.toString, sa.getRightExpression.toString)

    val parallelEqual = (a1Left == a2Left) && (a1Right == a2Right)
    val crossedEqual  = (a1Left == a2Right) && (a1Right == a2Left)

    val matchType: MatchType = if (parallelEqual || crossedEqual) {
      MatchType.SUCCESSFUL_MATCH
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }

    BinaryExpressionMatch(matchType, Some(ua), Some(sa))
  }
}
