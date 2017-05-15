package model.querycorrectors;

import java.util.LinkedList;
import java.util.List;

import model.matcher.BinaryExpressionMatcher;
import model.matching.MatchingResult;
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
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class WhereCorrector implements ExpressionVisitor {
  
  private static final BinaryExpressionMatcher BIN_EX_MATCHER = new BinaryExpressionMatcher();
  
  private boolean userQueryAnalyzed = false;
  private List<BinaryExpression> userExpressions = new LinkedList<>();
  
  private List<BinaryExpression> sampleExpressions = new LinkedList<>();
  
  public MatchingResult<BinaryExpression> correct(Expression userExpression, Expression sampleExpression) {
    sampleExpression.accept(this);
    userQueryAnalyzed = true;
    userExpression.accept(this);
    
    return BIN_EX_MATCHER.match("Bedingungen", userExpressions, sampleExpressions);
  }
  
  @Override
  public void visit(Addition addition) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(AllComparisonExpression allComparisonExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(AnalyticExpression aexpr) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(AndExpression andExpression) {
    andExpression.getLeftExpression().accept(this);
    andExpression.getRightExpression().accept(this);
  }
  
  @Override
  public void visit(AnyComparisonExpression anyComparisonExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Between between) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(BitwiseAnd bitwiseAnd) {
    if(userQueryAnalyzed)
      userExpressions.add(bitwiseAnd);
    else
      sampleExpressions.add(bitwiseAnd);
  }
  
  @Override
  public void visit(BitwiseOr bitwiseOr) {
    if(userQueryAnalyzed)
      userExpressions.add(bitwiseOr);
    else
      sampleExpressions.add(bitwiseOr);
  }
  
  @Override
  public void visit(BitwiseXor bitwiseXor) {
    if(userQueryAnalyzed)
      userExpressions.add(bitwiseXor);
    else
      sampleExpressions.add(bitwiseXor);
  }
  
  @Override
  public void visit(CaseExpression caseExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(CastExpression cast) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Column tableColumn) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Concat concat) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(DateTimeLiteralExpression dateTimeExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(DateValue dateValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Division division) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(DoubleValue doubleValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(EqualsTo equalsTo) {
    if(userQueryAnalyzed)
      userExpressions.add(equalsTo);
    else
      sampleExpressions.add(equalsTo);
  }
  
  @Override
  public void visit(ExistsExpression existsExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(ExtractExpression eexpr) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Function function) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(GreaterThan greaterThan) {
    if(userQueryAnalyzed)
      userExpressions.add(greaterThan);
    else
      sampleExpressions.add(greaterThan);
  }
  
  @Override
  public void visit(GreaterThanEquals greaterThanEquals) {
    if(userQueryAnalyzed)
      userExpressions.add(greaterThanEquals);
    else
      sampleExpressions.add(greaterThanEquals);
  }
  
  @Override
  public void visit(HexValue hexValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(InExpression inExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(IntervalExpression iexpr) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(IsNullExpression isNullExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(JdbcNamedParameter jdbcNamedParameter) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(JdbcParameter jdbcParameter) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(JsonExpression jsonExpr) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(KeepExpression aexpr) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(LikeExpression likeExpression) {
    if(userQueryAnalyzed)
      userExpressions.add(likeExpression);
    else
      sampleExpressions.add(likeExpression);
    
  }
  
  @Override
  public void visit(LongValue longValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Matches matches) {
    if(userQueryAnalyzed)
      userExpressions.add(matches);
    else
      sampleExpressions.add(matches);
  }
  
  @Override
  public void visit(MinorThan minorThan) {
    if(userQueryAnalyzed)
      userExpressions.add(minorThan);
    else
      sampleExpressions.add(minorThan);
  }
  
  @Override
  public void visit(MinorThanEquals minorThanEquals) {
    if(userQueryAnalyzed)
      userExpressions.add(minorThanEquals);
    else
      sampleExpressions.add(minorThanEquals);
  }
  
  @Override
  public void visit(Modulo modulo) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Multiplication multiplication) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(MySQLGroupConcat groupConcat) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(NotEqualsTo notEqualsTo) {
    if(userQueryAnalyzed)
      userExpressions.add(notEqualsTo);
    else
      sampleExpressions.add(notEqualsTo);
  }
  
  @Override
  public void visit(NullValue nullValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(NumericBind bind) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(OracleHierarchicalExpression oexpr) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(OracleHint hint) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(OrExpression orExpression) {
    orExpression.getLeftExpression().accept(this);
    orExpression.getRightExpression().accept(this);
  }
  
  @Override
  public void visit(Parenthesis parenthesis) {
    parenthesis.getExpression().accept(this);
  }
  
  @Override
  public void visit(RegExpMatchOperator rexpr) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(RegExpMySQLOperator regExpMySQLOperator) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(RowConstructor rowConstructor) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(SignedExpression signedExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(StringValue stringValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(SubSelect subSelect) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(Subtraction subtraction) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(TimeKeyExpression timeKeyExpression) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(TimestampValue timestampValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(TimeValue timeValue) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(UserVariable var) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(WhenClause whenClause) {
    // Ignore this type of expression
  }
  
  @Override
  public void visit(WithinGroupExpression wgexpr) {
    // Ignore this type of expression
  }
  
}
