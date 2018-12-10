package model.sql.matcher

import model.core.matching._
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.schema.Column
import play.api.libs.json.{JsString, JsValue}


final case class BinaryExpressionMatch(userArg: Option[BinaryExpression], sampleArg: Option[BinaryExpression]) extends Match[BinaryExpression] {

  override type AR = GenericAnalysisResult

  override def analyze(a1: BinaryExpression, a2: BinaryExpression): GenericAnalysisResult = {

    val (a1Left, a1Right) = (a1.getLeftExpression.toString, a1.getRightExpression.toString)
    val (a2Left, a2Right) = (a2.getLeftExpression.toString, a2.getRightExpression.toString)

    val parallelEqual = (a1Left == a2Left) && (a1Right == a2Right)
    val crossedEqual = (a1Left == a2Right) && (a1Right == a2Left)

    val matchType: MatchType = if (parallelEqual || crossedEqual) MatchType.SUCCESSFUL_MATCH
    else MatchType.UNSUCCESSFUL_MATCH

    GenericAnalysisResult(matchType)
  }

  override protected def descArgForJson(arg: BinaryExpression): JsValue = JsString(arg.toString)

}


class BinaryExpressionMatcher(userTAliases: Map[String, String], sampleTAliases: Map[String, String])
  extends Matcher[BinaryExpression, GenericAnalysisResult, BinaryExpressionMatch] {

  override protected val matchName: String = "Bedingungen"

  override protected val matchSingularName: String = "der Bedingung"

  private def getColToCompare(expression: BinaryExpression): Option[Column] = expression.getLeftExpression match {
    case left: Column =>
      Some(
        expression.getRightExpression match {
          case right: Column => if (left.toString < right.toString) left else right
          case _             => left
        })
    case _            =>
      expression.getRightExpression match {
        case right: Column => Some(right)
        case _             => None
      }
  }


  override protected def canMatch: (BinaryExpression, BinaryExpression) => Boolean = (binEx1, binEx2) => {

    def maybeTableAlias(col: Column): Option[String] = col.getTable match {
      case null  => None
      case table => Some(table.getName)
    }

    getColToCompare(binEx1) match {
      case None           => ???
      case Some(colComp1) =>
        getColToCompare(binEx2) match {
          case None           => ???
          case Some(colComp2) =>

            val table1 = maybeTableAlias(colComp1).map(a => userTAliases.getOrElse(a, a)).getOrElse("")
            val table2 = maybeTableAlias(colComp2).map(a => sampleTAliases.getOrElse(a, a)).getOrElse("")

            colComp1.getColumnName == colComp2.getColumnName && table1 == table2
        }
    }
  }

  override protected def matchInstantiation: (Option[BinaryExpression], Option[BinaryExpression]) => BinaryExpressionMatch = BinaryExpressionMatch

}
