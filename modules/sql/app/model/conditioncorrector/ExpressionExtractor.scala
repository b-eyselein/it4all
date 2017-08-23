package model.conditioncorrector;

import scala.collection.mutable.ListBuffer

import net.sf.jsqlparser.expression.AllComparisonExpression
import net.sf.jsqlparser.expression.AnalyticExpression
import net.sf.jsqlparser.expression.AnyComparisonExpression
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.expression.CaseExpression
import net.sf.jsqlparser.expression.CastExpression
import net.sf.jsqlparser.expression.DateTimeLiteralExpression
import net.sf.jsqlparser.expression.DateValue
import net.sf.jsqlparser.expression.DoubleValue
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.ExpressionVisitor
import net.sf.jsqlparser.expression.ExtractExpression
import net.sf.jsqlparser.expression.HexValue
import net.sf.jsqlparser.expression.IntervalExpression
import net.sf.jsqlparser.expression.JdbcNamedParameter
import net.sf.jsqlparser.expression.JdbcParameter
import net.sf.jsqlparser.expression.JsonExpression
import net.sf.jsqlparser.expression.KeepExpression
import net.sf.jsqlparser.expression.LongValue
import net.sf.jsqlparser.expression.MySQLGroupConcat
import net.sf.jsqlparser.expression.Function
import net.sf.jsqlparser.expression.NotExpression
import net.sf.jsqlparser.expression.NullValue
import net.sf.jsqlparser.expression.NumericBind
import net.sf.jsqlparser.expression.OracleHierarchicalExpression
import net.sf.jsqlparser.expression.OracleHint
import net.sf.jsqlparser.expression.Parenthesis
import net.sf.jsqlparser.expression.RowConstructor
import net.sf.jsqlparser.expression.SignedExpression
import net.sf.jsqlparser.expression.StringValue
import net.sf.jsqlparser.expression.TimeKeyExpression
import net.sf.jsqlparser.expression.TimeValue
import net.sf.jsqlparser.expression.TimestampValue
import net.sf.jsqlparser.expression.UserVariable
import net.sf.jsqlparser.expression.WhenClause
import net.sf.jsqlparser.expression.WithinGroupExpression
import net.sf.jsqlparser.expression.operators.arithmetic.Addition
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor
import net.sf.jsqlparser.expression.operators.arithmetic.Concat
import net.sf.jsqlparser.expression.operators.arithmetic.Division
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction
import net.sf.jsqlparser.expression.operators.conditional.AndExpression
import net.sf.jsqlparser.expression.operators.conditional.OrExpression
import net.sf.jsqlparser.expression.operators.relational.Between
import net.sf.jsqlparser.expression.operators.relational.EqualsTo
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression
import net.sf.jsqlparser.expression.operators.relational.GreaterThan
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals
import net.sf.jsqlparser.expression.operators.relational.InExpression
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression
import net.sf.jsqlparser.expression.operators.relational.JsonOperator
import net.sf.jsqlparser.expression.operators.relational.LikeExpression
import net.sf.jsqlparser.expression.operators.relational.Matches
import net.sf.jsqlparser.expression.operators.relational.MinorThan
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.select.SubSelect

case class ExtractedExpressions(binaryExpressions: List[BinaryExpression], otherExpressions: List[Expression]) {

  def isEmpty() = binaryExpressions.isEmpty && otherExpressions.isEmpty

}

class ExpressionExtractor(expression: Expression) extends ExpressionVisitor {

  val singleExpressions: ListBuffer[BinaryExpression] = new ListBuffer()
  val otherExpressions: ListBuffer[Expression] = new ListBuffer()

  // FIXME: compare compelte tree with and, or ...

  def extract: ExtractedExpressions = {
    if (expression != null)
      expression.accept(this)

    new ExtractedExpressions(singleExpressions.toList, otherExpressions.toList)
  }

  override def visit(addition: Addition)

  override def visit(allComparisonExpression: AllComparisonExpression)

  override def visit(aexpr: AnalyticExpression)

  override def visit(andExpression: AndExpression) {
    andExpression.getLeftExpression.accept(this)
    andExpression.getRightExpression.accept(this)
  }

  override def visit(anyComparisonExpression: AnyComparisonExpression)

  override def visit(between: Between)

  override def visit(bitwiseAnd: BitwiseAnd) = singleExpressions += bitwiseAnd

  override def visit(bitwiseOr: BitwiseOr) = singleExpressions += bitwiseOr

  override def visit(bitwiseXor: BitwiseXor) = singleExpressions += bitwiseXor

  override def visit(caseExpression: CaseExpression)

  override def visit(cast: CastExpression)

  override def visit(tableColumn: Column)

  override def visit(concat: Concat)

  override def visit(dateTimeExpression: DateTimeLiteralExpression)

  override def visit(dateValue: DateValue)

  override def visit(division: Division)

  override def visit(doubleValue: DoubleValue)

  override def visit(equalsTo: EqualsTo) = singleExpressions += equalsTo

  override def visit(existsExpression: ExistsExpression)

  override def visit(extractExpression: ExtractExpression)

  override def visit(function: Function)

  override def visit(greaterThan: GreaterThan)

  override def visit(greaterThanEquals: GreaterThanEquals)

  override def visit(hexValue: HexValue)

  override def visit(inExpression: InExpression)

  override def visit(iexpr: IntervalExpression)

  override def visit(isNullExpression: IsNullExpression)

  override def visit(jdbcNamedParameter: JdbcNamedParameter)

  override def visit(jdbcParameter: JdbcParameter)

  override def visit(jsonExpr: JsonExpression)

  override def visit(jsonOperator: JsonOperator)

  override def visit(aexpr: KeepExpression)

  override def visit(likeExpression: LikeExpression) = singleExpressions += likeExpression

  override def visit(longValue: LongValue)

  override def visit(matches: Matches) = singleExpressions += matches

  override def visit(minorThan: MinorThan) = singleExpressions += minorThan

  override def visit(minorThanEquals: MinorThanEquals) = singleExpressions += minorThanEquals

  override def visit(modulo: Modulo)

  override def visit(multiplication: Multiplication)

  override def visit(groupConcat: MySQLGroupConcat)

  override def visit(notEqualsTo: NotEqualsTo)

  override def visit(notExpression: NotExpression)

  override def visit(nullValue: NullValue)

  override def visit(bind: NumericBind)

  override def visit(oexpr: OracleHierarchicalExpression)

  override def visit(hint: OracleHint)

  override def visit(orExpression: OrExpression) {
    orExpression.getLeftExpression.accept(this)
    orExpression.getRightExpression.accept(this)
  }

  override def visit(parenthesis: Parenthesis) = parenthesis.getExpression.accept(this)

  override def visit(rexpr: RegExpMatchOperator)

  override def visit(regExpMySQLOperator: RegExpMySQLOperator)

  override def visit(rowConstructor: RowConstructor)

  override def visit(signedExpression: SignedExpression)

  override def visit(stringValue: StringValue)

  override def visit(subSelect: SubSelect)

  override def visit(subtraction: Subtraction)

  override def visit(timeKeyExpression: TimeKeyExpression)

  override def visit(timestampValue: TimestampValue)

  override def visit(timeValue: TimeValue)

  override def visit(variable: UserVariable)

  override def visit(whenClause: WhenClause)

  override def visit(wgexpr: WithinGroupExpression)
}
