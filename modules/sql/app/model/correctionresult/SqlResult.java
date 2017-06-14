package model.correctionresult;

import java.util.LinkedList;
import java.util.List;

import model.conditioncorrector.BinaryExpressionMatch;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.columnMatch.ColumnMatch;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class SqlResult<C> {
  
  private String learnerSolution;
  
  private MatchingResult<C, ColumnMatch<C>> columnComparison;
  
  private List<MatchingResult<String, Match<String>>> stringComparisons;
  
  private MatchingResult<BinaryExpression, BinaryExpressionMatch> whereComparison;
  
  private SqlExecutionResult executionResult;
  
  protected SqlResult(String theLearnerSolution, MatchingResult<C, ColumnMatch<C>> theColumnComparison,
      List<MatchingResult<String, Match<String>>> theStringComparisons,
      MatchingResult<BinaryExpression, BinaryExpressionMatch> theWhereComparison,
      SqlExecutionResult theExecutionResult) {
    learnerSolution = theLearnerSolution;
    columnComparison = theColumnComparison;
    stringComparisons = theStringComparisons;
    whereComparison = theWhereComparison;
    executionResult = theExecutionResult;
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
  
  public List<MatchingResult<String, Match<String>>> getListComparisons() {
    return stringComparisons;
  }
  
  public List<EvaluationResult> getResults() {
    List<EvaluationResult> ret = new LinkedList<>();
    ret.add(columnComparison);
    ret.add(whereComparison);
    ret.add(executionResult);
    ret.addAll(stringComparisons);
    return ret;
  }
  
  public MatchingResult<BinaryExpression, BinaryExpressionMatch> getWhereComparison() {
    return whereComparison;
  }
  
}
