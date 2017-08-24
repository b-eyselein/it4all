package model.conditioncorrector;

import scala.collection.mutable.ListBuffer

import net.sf.jsqlparser.expression._
import net.sf.jsqlparser.expression.operators.arithmetic._
import net.sf.jsqlparser.expression.operators.conditional.AndExpression
import net.sf.jsqlparser.expression.operators.conditional.OrExpression
import net.sf.jsqlparser.expression.operators.relational._
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.select.SubSelect

class ExpressionExtractor(expression: Expression) extends ExpressionVisitor {

  val binaryExpressions: ListBuffer[BinaryExpression] = new ListBuffer()

  // FIXME: compare compelte tree with and, or ...

  lazy val extracted = {
    if (expression != null)
      expression.accept(this)
    binaryExpressions.toList
  }

  override def visit(addition: Addition) = {}

  override def visit(allComparisonExpression: AllComparisonExpression) = {}

  override def visit(aexpr: AnalyticExpression) = {}

  override def visit(andExpression: AndExpression) {
    andExpression.getLeftExpression.accept(this)
    andExpression.getRightExpression.accept(this)
  }

  override def visit(anyComparisonExpression: AnyComparisonExpression) = {}

  override def visit(between: Between) = {}

  override def visit(bitwiseAnd: BitwiseAnd) = binaryExpressions += bitwiseAnd

  override def visit(bitwiseOr: BitwiseOr) = binaryExpressions += bitwiseOr

  override def visit(bitwiseXor: BitwiseXor) = binaryExpressions += bitwiseXor

  override def visit(caseExpression: CaseExpression) = {}

  override def visit(cast: CastExpression) = {}

  override def visit(tableColumn: Column) = {}

  override def visit(concat: Concat) = {}

  override def visit(dateTimeExpression: DateTimeLiteralExpression) = {}

  override def visit(dateValue: DateValue) = {}

  override def visit(division: Division) = {}

  override def visit(doubleValue: DoubleValue) = {}

  override def visit(equalsTo: EqualsTo) = binaryExpressions += equalsTo

  override def visit(existsExpression: ExistsExpression) = {}

  override def visit(extractExpression: ExtractExpression) = {}

  override def visit(function: Function) = {}

  override def visit(greaterThan: GreaterThan) = {}

  override def visit(greaterThanEquals: GreaterThanEquals) = {}

  override def visit(hexValue: HexValue) = {}

  override def visit(inExpression: InExpression) = {}

  override def visit(iexpr: IntervalExpression) = {}

  override def visit(isNullExpression: IsNullExpression) = {}

  override def visit(jdbcNamedParameter: JdbcNamedParameter) = {}

  override def visit(jdbcParameter: JdbcParameter) = {}

  override def visit(jsonExpr: JsonExpression) = {}

  override def visit(jsonOperator: JsonOperator) = {}

  override def visit(aexpr: KeepExpression) = {}

  override def visit(likeExpression: LikeExpression) = binaryExpressions += likeExpression

  override def visit(longValue: LongValue) = {}

  override def visit(matches: Matches) = binaryExpressions += matches

  override def visit(minorThan: MinorThan) = binaryExpressions += minorThan

  override def visit(minorThanEquals: MinorThanEquals) = binaryExpressions += minorThanEquals

  override def visit(modulo: Modulo) = {}

  override def visit(multiplication: Multiplication) = {}

  override def visit(groupConcat: MySQLGroupConcat) = {}

  override def visit(notEqualsTo: NotEqualsTo) = {}

  override def visit(notExpression: NotExpression) = {}

  override def visit(nullValue: NullValue) = {}

  override def visit(bind: NumericBind) = {}

  override def visit(oexpr: OracleHierarchicalExpression) = {}

  override def visit(hint: OracleHint) = {}

  override def visit(orExpression: OrExpression) {
    orExpression.getLeftExpression.accept(this)
    orExpression.getRightExpression.accept(this)
  }

  override def visit(parenthesis: Parenthesis) = parenthesis.getExpression.accept(this)

  override def visit(rexpr: RegExpMatchOperator) = {}

  override def visit(regExpMySQLOperator: RegExpMySQLOperator) = {}

  override def visit(rowConstructor: RowConstructor) = {}

  override def visit(signedExpression: SignedExpression) = {}

  override def visit(stringValue: StringValue) = {}

  override def visit(subSelect: SubSelect) = {}

  override def visit(subtraction: Subtraction) = {}

  override def visit(timeKeyExpression: TimeKeyExpression) = {}

  override def visit(timestampValue: TimestampValue) = {}

  override def visit(timeValue: TimeValue) = {}

  override def visit(variable: UserVariable) = {}

  override def visit(whenClause: WhenClause) = {}

  override def visit(wgexpr: WithinGroupExpression) = {}
}
