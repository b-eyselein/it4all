package model.conditioncorrector

import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column

class ExpressionBiPredicate(userTAliases: Map[String, String], sampleTAliases: Map[String, String]) extends Function2[Expression, Expression, Boolean] {

  override def apply(binEx1: Expression, binEx2: Expression) = {
    val completeColumn1 = getColumnToCompare(binEx1)
    val completeColumn2 = getColumnToCompare(binEx2)

    val column1 = completeColumn1.getColumnName
    val column2 = completeColumn2.getColumnName

    val tableAlias1 = completeColumn1.getTable.getName
    val tableAlias2 = completeColumn2.getTable.getName

    val table1 = if (tableAlias1 == null) "" else userTAliases.getOrElse(tableAlias1, tableAlias1)
    val table2 = if (tableAlias2 == null) "" else sampleTAliases.getOrElse(tableAlias2, tableAlias2)

    column1 == column2 && table1 == table2
  }

  def compareLeftExpression(expression: BinaryExpression) = {
    val leftExp = expression.getLeftExpression
    val rightExp = expression.getRightExpression

    !(rightExp.isInstanceOf[Column]) || (leftExp.isInstanceOf[Column] && leftExp.toString.compareTo(rightExp.toString) < 0)
  }

  def getColumnToCompare(expression: Expression) =
    expression match {
      case b: BinaryExpression => if (compareLeftExpression(b)) b.getLeftExpression.asInstanceOf[Column] else b.getRightExpression.asInstanceOf[Column]
      case _ => null
    }

}
