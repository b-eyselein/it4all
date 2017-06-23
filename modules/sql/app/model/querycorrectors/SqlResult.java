package model.querycorrectors;

import java.util.Arrays;
import java.util.List;

import model.conditioncorrector.BinaryExpressionMatch;
import model.matching.Match;
import model.matching.MatchingResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.statement.Statement;
import play.twirl.api.Html;

public abstract class SqlResult<Q extends Statement, C> {

  protected String learnerSolution;

  protected MatchingResult<C, ColumnMatch<C>> columnComparison;

  protected MatchingResult<BinaryExpression, BinaryExpressionMatch> whereComparison;

  protected SqlExecutionResult executionResult;

  protected MatchingResult<String, Match<String>> tableComparison;

  public SqlResult(String theLearnerSolution) {
    learnerSolution = theLearnerSolution;
  }

  public MatchingResult<C, ColumnMatch<C>> getColumnComparison() {
    return columnComparison;
  }

  public SqlExecutionResult getExecutionResult() {
    return executionResult;
  }

  public String getLearnerSolution() {
    return learnerSolution;
  }

  public List<EvaluationResult> getResults() {
    return Arrays.asList(columnComparison, whereComparison, executionResult, tableComparison);
  }

  public MatchingResult<String, Match<String>> getTableComparison() {
    return tableComparison;
  }

  public MatchingResult<BinaryExpression, BinaryExpressionMatch> getWhereComparison() {
    return whereComparison;
  }

  public abstract SqlResult<Q, C> makeOtherComparisons(Q userQ, Q sampleQ);

  public abstract Html render();

  public SqlResult<Q, C> setColumnComparison(MatchingResult<C, ColumnMatch<C>> theColumnComparison) {
    columnComparison = theColumnComparison;
    return this;
  }

  public SqlResult<Q, C> setExecutionResult(SqlExecutionResult theExecutionResult) {
    executionResult = theExecutionResult;
    return this;
  }

  public SqlResult<Q, C> setTableComparison(MatchingResult<String, Match<String>> theTableComparison) {
    tableComparison = theTableComparison;
    return this;
  }

  public SqlResult<Q, C> setWhereComparison(
      MatchingResult<BinaryExpression, BinaryExpressionMatch> theWhereComparison) {
    whereComparison = theWhereComparison;
    return this;
  }

}
