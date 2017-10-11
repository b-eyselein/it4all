package model.conditioncorrector

import model.{CorrectionException, StringConsts}
import model.matching.{Match, MatchType, Matcher}
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.schema.Column

object BinaryExpressionMatcher {

  def compareExpressions(binEx1: BinaryExpression, userTAliases: Map[String, String],
                         binEx2: BinaryExpression, sampleTAliases: Map[String, String]): Boolean = {

    def colNameAndAlias(col: Column) = (col.getColumnName, col.getTable.getName)

    val (column1, alias1) = colNameAndAlias(getColToCompare(binEx1))
    val (column2, alias2) = colNameAndAlias(getColToCompare(binEx2))

    val table1 = if (alias1 == null) "" else userTAliases.getOrElse(alias1, alias1)
    val table2 = if (alias2 == null) "" else sampleTAliases.getOrElse(alias2, alias2)

    column1 == column2 && table1 == table2
  }

  def getColToCompare(expression: BinaryExpression): Column = (expression.getLeftExpression, expression.getRightExpression) match {
    case (left: Column, right: Column) => if (left.toString < right.toString) left else right
    case (left, right: Column) => right
    case (left: Column, right) => left
    case (_, _) => throw new CorrectionException("", "")
  }

}

class BinaryExpressionMatcher(userTAliases: Map[String, String], sampleTAliases: Map[String, String])
  extends Matcher[BinaryExpression, BinaryExpressionMatch](
    StringConsts.CONDITIONS_NAME, List(""), BinaryExpressionMatcher.compareExpressions(_, userTAliases, _, sampleTAliases),
    BinaryExpressionMatch)

case class BinaryExpressionMatch(arg1: Option[BinaryExpression], arg2: Option[BinaryExpression], s: Int)
  extends Match[BinaryExpression](arg1, arg2, s) {

  //  can only be successful match
  override def analyze(a1: BinaryExpression, a2: BinaryExpression) = MatchType.SUCCESSFUL_MATCH

}