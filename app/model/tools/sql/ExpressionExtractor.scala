package model.tools.sql

import net.sf.jsqlparser.expression._
import net.sf.jsqlparser.expression.operators.arithmetic._
import net.sf.jsqlparser.expression.operators.conditional.{AndExpression, OrExpression, XorExpression}
import net.sf.jsqlparser.expression.operators.relational._
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.select.{AllColumns, AllTableColumns, SubSelect}

import scala.collection.mutable.ListBuffer

class ExpressionExtractor extends ExpressionVisitor {

  private val binaryExpressions: ListBuffer[BinaryExpression] = new ListBuffer()

  // FIXME: compare complete tree with and, or ...

  def extractFrom(expression: Expression): Seq[BinaryExpression] = {

    if (expression != null) {
      expression accept this
    }

    binaryExpressions.toList
  }

  override def visit(addition: Addition): Unit = {}

  override def visit(analyticExpression: AnalyticExpression): Unit = {}

  override def visit(andExpression: AndExpression): Unit = {
    andExpression.getLeftExpression accept this
    andExpression.getRightExpression accept this
  }

  override def visit(anyComparisonExpression: AnyComparisonExpression): Unit = {}

  override def visit(between: Between): Unit = {}

  override def visit(bitwiseAnd: BitwiseAnd): Unit = binaryExpressions += bitwiseAnd

  override def visit(bitwiseOr: BitwiseOr): Unit = binaryExpressions += bitwiseOr

  override def visit(bitwiseXor: BitwiseXor): Unit = binaryExpressions += bitwiseXor

  override def visit(caseExpression: CaseExpression): Unit = {}

  override def visit(cast: CastExpression): Unit = {}

  override def visit(tableColumn: Column): Unit = {}

  override def visit(concat: Concat): Unit = {}

  override def visit(dateTimeExpression: DateTimeLiteralExpression): Unit = {}

  override def visit(dateValue: DateValue): Unit = {}

  override def visit(division: Division): Unit = {}

  override def visit(doubleValue: DoubleValue): Unit = {}

  override def visit(equalsTo: EqualsTo): Unit = binaryExpressions += equalsTo

  override def visit(existsExpression: ExistsExpression): Unit = {}

  override def visit(extractExpression: ExtractExpression): Unit = {}

  override def visit(function: Function): Unit = {}

  override def visit(greaterThan: GreaterThan): Unit = {}

  override def visit(greaterThanEquals: GreaterThanEquals): Unit = {}

  override def visit(hexValue: HexValue): Unit = {}

  override def visit(inExpression: InExpression): Unit = {}

  override def visit(iexpr: IntervalExpression): Unit = {}

  override def visit(isNullExpression: IsNullExpression): Unit = {}

  override def visit(jdbcNamedParameter: JdbcNamedParameter): Unit = {}

  override def visit(jdbcParameter: JdbcParameter): Unit = {}

  override def visit(jsonExpr: JsonExpression): Unit = {}

  override def visit(jsonOperator: JsonOperator): Unit = {}

  override def visit(aexpr: KeepExpression): Unit = {}

  override def visit(likeExpression: LikeExpression): Unit = binaryExpressions += likeExpression

  override def visit(longValue: LongValue): Unit = {}

  override def visit(matches: Matches): Unit = binaryExpressions += matches

  override def visit(minorThan: MinorThan): Unit = binaryExpressions += minorThan

  override def visit(minorThanEquals: MinorThanEquals): Unit = binaryExpressions += minorThanEquals

  override def visit(modulo: Modulo): Unit = {}

  override def visit(multiplication: Multiplication): Unit = {}

  override def visit(groupConcat: MySQLGroupConcat): Unit = {}

  override def visit(notEqualsTo: NotEqualsTo): Unit = {}

  override def visit(notExpression: NotExpression): Unit = {}

  override def visit(nullValue: NullValue): Unit = {}

  override def visit(bind: NumericBind): Unit = {}

  override def visit(oexpr: OracleHierarchicalExpression): Unit = {}

  override def visit(hint: OracleHint): Unit = {}

  override def visit(orExpression: OrExpression): Unit = {
    orExpression.getLeftExpression.accept(this)
    orExpression.getRightExpression.accept(this)
  }

  override def visit(orExpression: XorExpression): Unit = {
    orExpression.getLeftExpression.accept(this)
    orExpression.getRightExpression.accept(this)
  }

  override def visit(parenthesis: Parenthesis): Unit = parenthesis.getExpression.accept(this)

  override def visit(rexpr: RegExpMatchOperator): Unit = {}

  override def visit(regExpMySQLOperator: RegExpMySQLOperator): Unit = {}

  override def visit(rowConstructor: RowConstructor): Unit = {}

  override def visit(signedExpression: SignedExpression): Unit = {}

  override def visit(stringValue: StringValue): Unit = {}

  override def visit(subSelect: SubSelect): Unit = {}

  override def visit(subtraction: Subtraction): Unit = {}

  override def visit(timeKeyExpression: TimeKeyExpression): Unit = {}

  override def visit(timestampValue: TimestampValue): Unit = {}

  override def visit(timeValue: TimeValue): Unit = {}

  override def visit(variable: UserVariable): Unit = {}

  override def visit(whenClause: WhenClause): Unit = {}

  override def visit(aThis: BitwiseRightShift): Unit = {}

  override def visit(aThis: BitwiseLeftShift): Unit = {}

  override def visit(valueList: ValueListExpression): Unit = {}

  override def visit(aThis: NextValExpression): Unit = {}

  override def visit(aThis: CollateExpression): Unit = {}

  override def visit(aThis: SimilarToExpression): Unit = {}

  override def visit(division: IntegerDivision): Unit = {}

  override def visit(fullTextSearch: FullTextSearch): Unit = {}

  override def visit(isBooleanExpression: IsBooleanExpression): Unit = {}

  override def visit(aThis: ArrayExpression): Unit = {}

  override def visit(xmlSerializeExpression: XMLSerializeExpr): Unit = {}

  override def visit(variableAssignment: VariableAssignment): Unit = {}

  override def visit(rowGetExpression: RowGetExpression): Unit = {}

  override def visit(aThis: ArrayConstructor): Unit = {}

  override def visit(aThis: TimezoneExpression): Unit = {}

  override def visit(aThis: JsonAggregateFunction): Unit = {}

  override def visit(aThis: JsonFunction): Unit = {}

  override def visit(aThis: ConnectByRootOperator): Unit = {}

  override def visit(aThis: OracleNamedFunctionParameter): Unit = {}

  override def visit(cast: TryCastExpression): Unit = {}

  override def visit(allColumns: AllColumns): Unit = {}

  override def visit(allTableColumns: AllTableColumns): Unit = {}

  override def visit(allValue: AllValue): Unit = {}

  override def visit(isDistinctExpression: IsDistinctExpression): Unit = {}

  override def visit(geometryDistance: GeometryDistance): Unit = {}

  override def visit(overlapsCondition: OverlapsCondition): Unit = {}

  override def visit(cast: SafeCastExpression): Unit = {}

}
