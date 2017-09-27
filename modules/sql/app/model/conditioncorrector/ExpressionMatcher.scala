package model.conditioncorrector

import model.StringConsts
import model.matching.Matcher
import net.sf.jsqlparser.expression.Expression
import model.matching.MatchingResult
import model.matching.Match
import model.matching.MatchType
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.expression.BinaryExpression
import model.CorrectionException

object BinaryExpressionMatcher {

  def compareExpressions(
    binEx1: BinaryExpression, userTAliases: Map[String, String],
    binEx2: BinaryExpression, sampleTAliases: Map[String, String]) = {

    def colNameAndAlias(col: Column) = (col.getColumnName, col.getTable.getName)

    val (column1, alias1) = colNameAndAlias(getColToCompare(binEx1))
    val (column2, alias2) = colNameAndAlias(getColToCompare(binEx2))

    val table1 = if (alias1 == null) "" else userTAliases.getOrElse(alias1, alias1)
    val table2 = if (alias2 == null) "" else sampleTAliases.getOrElse(alias2, alias2)

    column1 == column2 && table1 == table2
  }

  def getColToCompare(expression: BinaryExpression) = (expression.getLeftExpression, expression.getRightExpression) match {
    case (left: Column, right: Column) => if (left.toString < right.toString) left else right
    case (left, right: Column) => right
    case (left: Column, right) => left
    case (_, _) => throw new CorrectionException("", "")
  }

}

class BinaryExpressionMatcher(userTAliases: Map[String, String], sampleTAliases: Map[String, String])
  extends Matcher[BinaryExpression, BinaryExpressionMatch](
    StringConsts.CONDITIONS_NAME,
    List(""),
    BinaryExpressionMatcher.compareExpressions(_, userTAliases, _, sampleTAliases),
    new BinaryExpressionMatch(_, _))

class BinaryExpressionMatch(arg1: Option[BinaryExpression], arg2: Option[BinaryExpression])
  extends Match[BinaryExpression](arg1, arg2) {

  //  can only be successful match
  override def analyze(a1: BinaryExpression, a2: BinaryExpression) = MatchType.SUCCESSFUL_MATCH

}