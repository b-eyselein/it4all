package model.correctionresult;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import model.matching.MatchingResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class SqlResult {
  
  private MatchingResult<String> columnComparison;
  
  private MatchingResult<String> tableComparison;
  
  private MatchingResult<String> orderByComparison;
  
  private MatchingResult<String> groupByComparison;
  
  private MatchingResult<BinaryExpression> whereComparison;
  
  private SqlExecutionResult executionResult;
  
  // @formatter:off
  protected SqlResult(
      MatchingResult<String> theColumnComparison,
      MatchingResult<String> theTableComparison,
      MatchingResult<String> theOrderByComparison,
      MatchingResult<String> theGroupByComparison,

      MatchingResult<BinaryExpression> theWhereComparison,

      SqlExecutionResult theExecutionResult) {
    columnComparison = theColumnComparison;
    tableComparison = theTableComparison;
    orderByComparison = theOrderByComparison;
    groupByComparison = theGroupByComparison;

    whereComparison = theWhereComparison;

    executionResult = theExecutionResult;
  }
  // @formatter:on
  
  public MatchingResult<String> getColumnComparison() {
    return columnComparison;
  }
  
  public SqlExecutionResult getExecutionResult() {
    return executionResult;
  }
  
  public MatchingResult<String> getGroupByComparison() {
    return groupByComparison;
  }
  
  public List<MatchingResult<String>> getListComparisons() {
    return Arrays.asList(columnComparison, tableComparison, orderByComparison, groupByComparison).stream()
        .filter(Objects::nonNull).collect(Collectors.toList());
  }
  
  public MatchingResult<String> getOrderByComparison() {
    return orderByComparison;
  }
  
  public List<EvaluationResult> getResults() {
    return Arrays.asList(columnComparison, tableComparison, orderByComparison, groupByComparison, whereComparison,
        executionResult);
  }
  
  public MatchingResult<String> getTableComparison() {
    return tableComparison;
  }
  
  public MatchingResult<BinaryExpression> getWhereComparison() {
    return whereComparison;
  }
  
}
