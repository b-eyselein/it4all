package model.sql.matcher

import model.core.matching._
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.schema.Column
import play.api.libs.json.{JsString, JsValue}


case class BinaryExpressionMatch(userArg: Option[BinaryExpression], sampleArg: Option[BinaryExpression]) extends Match[BinaryExpression, GenericAnalysisResult] {

  //  override type MatchAnalysisResult = GenericAnalysisResult

  override def analyze(a1: BinaryExpression, a2: BinaryExpression): GenericAnalysisResult = {

    val (a1Left, a1Right) = (a1.getLeftExpression.toString, a1.getRightExpression.toString)
    val (a2Left, a2Right) = (a2.getLeftExpression.toString, a2.getRightExpression.toString)

    val parallelEqual = (a1Left == a2Left) && (a1Right == a2Right)
    val crossedEqual = (a1Left == a2Right) && (a1Right == a2Left)

    val matchType = if (parallelEqual || crossedEqual) MatchType.SUCCESSFUL_MATCH
    else MatchType.UNSUCCESSFUL_MATCH

    GenericAnalysisResult(matchType)
  }

  override protected def descArgForJson(arg: BinaryExpression): JsValue = JsString(arg.toString)

}


class BinaryExpressionMatcher(userTAliases: Map[String, String], sampleTAliases: Map[String, String])
  extends Matcher[BinaryExpression, GenericAnalysisResult, BinaryExpressionMatch] {

  private def getColToCompare(expression: BinaryExpression): Column = (expression.getLeftExpression, expression.getRightExpression) match {
    case (left: Column, right: Column) => if (left.toString < right.toString) left else right
    case (_, right: Column)            => right
    case (left: Column, _)             => left
    case (_, _)                        => null // throw new CorrectionException("", "")
  }

  override protected def canMatch: (BinaryExpression, BinaryExpression) => Boolean = (binEx1, binEx2) => {

    def colNameAndAlias(col: Column) = (col.getColumnName, col.getTable.getName)

    val (column1, alias1) = colNameAndAlias(getColToCompare(binEx1))
    val (column2, alias2) = colNameAndAlias(getColToCompare(binEx2))

    val table1 = if (alias1 == null) "" else userTAliases.getOrElse(alias1, alias1)
    val table2 = if (alias2 == null) "" else sampleTAliases.getOrElse(alias2, alias2)

    column1 == column2 && table1 == table2
  }


  override protected def matchInstantiation: (Option[BinaryExpression], Option[BinaryExpression]) => BinaryExpressionMatch = BinaryExpressionMatch

}
