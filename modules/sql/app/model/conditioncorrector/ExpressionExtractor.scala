package model.conditioncorrector;

import java.util.LinkedList;
import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.WithinGroupExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;
import scala.collection.mutable.ListBuffer

class ExpressionExtractor(expression: Expression) extends ExpressionVisitor {

  val singleExpressions: ListBuffer[BinaryExpression] = new ListBuffer()
  val otherExpressions: ListBuffer[Expression] = new ListBuffer()

  // FIXME: compare compelte tree with and, or ...

  def extract: ExtractedExpressions = {
    if (expression != null)
      expression.accept(this)

    new ExtractedExpressions(singleExpressions.toList, otherExpressions.toList)
  }

  override def visit(addition: Addition) {
    // Ignore this type of expression
  }

  override def visit(allComparisonExpression: AllComparisonExpression) {
    // Ignore this type of expression
  }

  override def visit(aexpr: AnalyticExpression) {
    // Ignore this type of expression
  }

  override def visit(andExpression: AndExpression) {
    andExpression.getLeftExpression.accept(this)
    andExpression.getRightExpression.accept(this)
  }

  override def visit(anyComparisonExpression: AnyComparisonExpression) {
    // Ignore this type of expression
  }

  override def visit(between: Between) {
    // Ignore this type of expression
  }

  override def visit(bitwiseAnd: BitwiseAnd) {
    singleExpressions += bitwiseAnd
  }

  override def visit(bitwiseOr: BitwiseOr) {
    singleExpressions += bitwiseOr
  }

  override def visit(bitwiseXor: BitwiseXor) {
    singleExpressions += bitwiseXor
  }

  override def visit(caseExpression: CaseExpression) {
    // Ignore this type of expression
  }

  override def visit(cast: CastExpression) {
    // Ignore this type of expression
  }

  override def visit(tableColumn: Column) {
    // Ignore this type of expression
  }

  override def visit(concat: Concat) {
    // Ignore this type of expression
  }

  override def visit(dateTimeExpression: DateTimeLiteralExpression) {
    // Ignore this type of expression
  }

  override def visit(dateValue: DateValue) {
    // Ignore this type of expression
  }

  override def visit(division: Division) {
    // Ignore this type of expression
  }

  override def visit(doubleValue: DoubleValue) {
    // Ignore this type of expression
  }

  override def visit(equalsTo: EqualsTo) {
    singleExpressions += equalsTo
  }

  override def visit(existsExpression: ExistsExpression) {
    // Ignore this type of expression
  }

  override def visit(extractExpression: ExtractExpression) {
    // Ignore this type of expression
  }

  override def visit(function: Function) {
    // Ignore this type of expression
  }

  override def visit(greaterThan: GreaterThan) {
    singleExpressions += greaterThan
  }

  override def visit(greaterThanEquals: GreaterThanEquals) {
    singleExpressions += greaterThanEquals
  }

  override def visit(hexValue: HexValue) {
    // Ignore this type of expression
  }

  override def visit(inExpression: InExpression) {
    // Ignore this type of expression
  }

  override def visit(iexpr: IntervalExpression) {
    // Ignore this type of expression
  }

  override def visit(isNullExpression: IsNullExpression) {
    otherExpressions += isNullExpression
  }

  override def visit(jdbcNamedParameter: JdbcNamedParameter) {
    // Ignore this type of expression
  }

  override def visit(jdbcParameter: JdbcParameter) {
    // Ignore this type of expression
  }

  override def visit(jsonExpr: JsonExpression) {
    // Ignore this type of expression
  }

  override def visit(jsonOperator: JsonOperator) {
    // Ignore this type of expression
  }

  override def visit(aexpr: KeepExpression) {
    // Ignore this type of expression
  }

  override def visit(likeExpression: LikeExpression) {
    singleExpressions += likeExpression

  }

  override def visit(longValue: LongValue) {
    // Ignore this type of expression
  }

  override def visit(matches: Matches) {
    singleExpressions += matches
  }

  override def visit(minorThan: MinorThan) {
    singleExpressions += minorThan
  }

  override def visit(minorThanEquals: MinorThanEquals) {
    singleExpressions += minorThanEquals
  }

  override def visit(modulo: Modulo) {
    // Ignore this type of expression
  }

  override def visit(multiplication: Multiplication) {
    // Ignore this type of expression
  }

  override def visit(groupConcat: MySQLGroupConcat) {
    // Ignore this type of expression
  }

  override def visit(notEqualsTo: NotEqualsTo) {
    singleExpressions += notEqualsTo
  }

  override def visit(notExpression: NotExpression) {
    // Ignore this type of expression
  }

  override def visit(nullValue: NullValue) {
    // Ignore this type of expression
  }

  override def visit(bind: NumericBind) {
    // Ignore this type of expression
  }

  override def visit(oexpr: OracleHierarchicalExpression) {
    // Ignore this type of expression
  }

  override def visit(hint: OracleHint) {
    // Ignore this type of expression
  }

  override def visit(orExpression: OrExpression) {
    orExpression.getLeftExpression.accept(this)
    orExpression.getRightExpression.accept(this)
  }

  override def visit(parenthesis: Parenthesis) {
    parenthesis.getExpression.accept(this)
  }

  override def visit(rexpr: RegExpMatchOperator) {
    // Ignore this type of expression
  }

  override def visit(regExpMySQLOperator: RegExpMySQLOperator) {
    // Ignore this type of expression
  }

  override def visit(rowConstructor: RowConstructor) {
    // Ignore this type of expression
  }

  override def visit(signedExpression: SignedExpression) {
    // Ignore this type of expression
  }

  override def visit(stringValue: StringValue) {
    // Ignore this type of expression
  }

  override def visit(subSelect: SubSelect) {
    // Ignore this type of expression
  }

  override def visit(subtraction: Subtraction) {
    // Ignore this type of expression
  }

  override def visit(timeKeyExpression: TimeKeyExpression) {
    // Ignore this type of expression
  }

  override def visit(timestampValue: TimestampValue) {
    // Ignore this type of expression
  }

  override def visit(timeValue: TimeValue) {
    // Ignore this type of expression
  }

  override def visit(variable: UserVariable) {
    // Ignore this type of expression
  }

  override def visit(whenClause: WhenClause) {
    // Ignore this type of expression
  }

  override def visit(wgexpr: WithinGroupExpression) {
    // Ignore this type of expression
  }
}
