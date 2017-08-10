package model.querycorrectors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import model.exercise.Success;
import model.matching.Match;
import model.matching.MatchingResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.Expression;
import play.twirl.api.Html;

public class SqlResult<C> extends EvaluationResult {
  
  protected String learnerSolution;
  
  protected MatchingResult<C, ColumnMatch<C>> columnComparison;
  
  protected MatchingResult<Expression, Match<Expression>> whereComparison;
  
  protected SqlExecutionResult executionResult;
  
  protected MatchingResult<String, Match<String>> tableComparison;
  
  protected List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> otherComparisons = new LinkedList<>();

  public SqlResult(String theLearnerSolution) {
    super(Success.NONE);
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

  public List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> getMatchingResults() {
    List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> ret = new LinkedList<>();
    ret.addAll(Arrays.asList(columnComparison, tableComparison, whereComparison));
    ret.addAll(otherComparisons);
    return ret;
  }

  public List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> getOtherComparisons() {
    return otherComparisons;
  }
  
  public List<EvaluationResult> getResults() {
    return Arrays.asList(columnComparison, whereComparison, executionResult, tableComparison);
  }
  
  public MatchingResult<String, Match<String>> getTableComparison() {
    return tableComparison;
  }
  
  public MatchingResult<Expression, Match<Expression>> getWhereComparison() {
    return whereComparison;
  }
  
  public Html render() {
    return views.html.resultTemplates.sqlResult.render(this);
  }
  
  public SqlResult<C> setColumnComparison(MatchingResult<C, ColumnMatch<C>> theColumnComparison) {
    columnComparison = theColumnComparison;
    return this;
  }
  
  public SqlResult<C> setExecutionResult(SqlExecutionResult theExecutionResult) {
    executionResult = theExecutionResult;
    return this;
  }
  
  public SqlResult<C> setOtherComparisons(
      List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> theOtherComparisons) {
    otherComparisons = theOtherComparisons;
    return this;
  }
  
  public SqlResult<C> setTableComparison(MatchingResult<String, Match<String>> theTableComparison) {
    tableComparison = theTableComparison;
    return this;
  }

  public SqlResult<C> setWhereComparison(MatchingResult<Expression, Match<Expression>> theWhereComparison) {
    whereComparison = theWhereComparison;
    return this;
  }
  
}
