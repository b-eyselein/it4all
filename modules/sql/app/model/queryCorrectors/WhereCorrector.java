package model.queryCorrectors;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.Matcher;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
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

  private boolean userQueryAnalyzed = false;

  private List<EqualsTo> userEquals = new LinkedList<>();
  private List<EqualsTo> sampleEquals = new LinkedList<>();

  private List<LikeExpression> userLikes = new LinkedList<>();
  private List<LikeExpression> sampleLikes = new LinkedList<>();

  // FIXME: compare where conditions!
  public EvaluationResult correct(Expression userExpression, Expression sampleExpression) {
    // TODO Auto-generated method stub
    sampleExpression.accept(this);
    userQueryAnalyzed = true;
    userExpression.accept(this);

    Comparator<EqualsTo> comparator = new Comparator<EqualsTo>() {

      @Override
      public int compare(EqualsTo arg0, EqualsTo arg1) {
        // TODO Auto-generated method stub
        return arg0.getLeftExpression().toString().compareTo(arg1.getLeftExpression().toString());
      }
    };

    Matcher<EqualsTo> equalsMatcher = new Matcher<EqualsTo>(userEquals, sampleEquals, comparator);

    // Logger.debug(userEquals.toString());
    // Logger.debug(sampleEquals.toString());
    //
    // Logger.debug(userLikes.toString());
    // Logger.debug(sampleLikes.toString());

    return new EvaluationFailed("WHERE-Comparison has to be implemented first...");
  }

  @Override
  public void visit(Addition addition) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(AllComparisonExpression allComparisonExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(AnalyticExpression aexpr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(AndExpression andExpression) {
    andExpression.getLeftExpression().accept(this);
    andExpression.getRightExpression().accept(this);
  }

  @Override
  public void visit(AnyComparisonExpression anyComparisonExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Between between) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BitwiseAnd bitwiseAnd) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BitwiseOr bitwiseOr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BitwiseXor bitwiseXor) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(CaseExpression caseExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(CastExpression cast) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Column tableColumn) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Concat concat) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(DateValue dateValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Division division) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(DoubleValue doubleValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(EqualsTo equalsTo) {
    // TODO Auto-generated method stub
    if(userQueryAnalyzed)
      userEquals.add(equalsTo);
    else
      sampleEquals.add(equalsTo);
    // System.out.println("EqualsTo: " + equalsTo.toString() + "\n\tLeft: " +
    // equalsTo.getLeftExpression().getClass()
    // + "\n\tRight: " + equalsTo.getRightExpression().getClass());
  }

  @Override
  public void visit(ExistsExpression existsExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ExtractExpression eexpr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Function function) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(GreaterThan greaterThan) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(GreaterThanEquals greaterThanEquals) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(HexValue hexValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(InExpression inExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(IntervalExpression iexpr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(IsNullExpression isNullExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(JdbcNamedParameter jdbcNamedParameter) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(JdbcParameter jdbcParameter) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(JsonExpression jsonExpr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(KeepExpression aexpr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(LikeExpression likeExpression) {
    // TODO Auto-generated method stub
    if(userQueryAnalyzed)
      userLikes.add(likeExpression);
    else
      sampleLikes.add(likeExpression);
    // System.out.println(
    // "LikeExpression: " + likeExpression.toString() + "\n\tLeft: " +
    // likeExpression.getLeftExpression().getClass()
    // + "\n\tRight: " + likeExpression.getRightExpression().getClass());

  }

  @Override
  public void visit(LongValue longValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Matches matches) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(MinorThan minorThan) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(MinorThanEquals minorThanEquals) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Modulo modulo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Multiplication multiplication) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(MySQLGroupConcat groupConcat) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(NotEqualsTo notEqualsTo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(NullValue nullValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(NumericBind bind) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(OracleHierarchicalExpression oexpr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(OracleHint hint) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(OrExpression orExpression) {
    // TODO Auto-generated method stub
    orExpression.getLeftExpression().accept(this);
    orExpression.getRightExpression().accept(this);
  }

  @Override
  public void visit(Parenthesis parenthesis) {
    parenthesis.getExpression().accept(this);
  }

  @Override
  public void visit(RegExpMatchOperator rexpr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(RegExpMySQLOperator regExpMySQLOperator) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(RowConstructor rowConstructor) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(SignedExpression signedExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(StringValue stringValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(SubSelect subSelect) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Subtraction subtraction) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(TimestampValue timestampValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(TimeValue timeValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(UserVariable var) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(WhenClause whenClause) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(WithinGroupExpression wgexpr) {
    // TODO Auto-generated method stub

  }

}
