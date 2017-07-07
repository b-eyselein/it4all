package model.querycorrectors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import model.matching.Match;
import model.matching.MatchingResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import play.twirl.api.Html;

public class SqlResult<Q extends Statement, C> {
  
  protected String learnerSolution;
  
  protected MatchingResult<C, ColumnMatch<C>> columnComparison;
  
  protected MatchingResult<Expression, Match<Expression>> whereComparison;
  
  protected SqlExecutionResult executionResult;
  
  protected MatchingResult<String, Match<String>> tableComparison;
  
  protected List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> otherComparisons = new LinkedList<>();

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
  
  public SqlResult<Q, C> setColumnComparison(MatchingResult<C, ColumnMatch<C>> theColumnComparison) {
    columnComparison = theColumnComparison;
    return this;
  }
  
  public SqlResult<Q, C> setExecutionResult(SqlExecutionResult theExecutionResult) {
    executionResult = theExecutionResult;
    return this;
  }
  
  public SqlResult<Q, C> setOtherComparisons(
      List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> theOtherComparisons) {
    otherComparisons = theOtherComparisons;
    return this;
  }
  
  public SqlResult<Q, C> setTableComparison(MatchingResult<String, Match<String>> theTableComparison) {
    tableComparison = theTableComparison;
    return this;
  }

  public SqlResult<Q, C> setWhereComparison(MatchingResult<Expression, Match<Expression>> theWhereComparison) {
    whereComparison = theWhereComparison;
    return this;
  }
  
}
