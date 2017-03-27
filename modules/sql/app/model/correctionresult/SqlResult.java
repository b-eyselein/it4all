package model.correctionresult;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import model.result.EvaluationResult;

public class SqlResult {
  
  private ComparisonTwoListsOfStrings columnComparison;
  
  private ComparisonTwoListsOfStrings tableComparison;
  
  private ComparisonTwoListsOfStrings orderByComparison;
  
  private ComparisonTwoListsOfStrings groupByComparison;
  
  private WhereComparison whereComparison;
  
  private SqlExecutionResult executionResult;
  
  protected SqlResult(ComparisonTwoListsOfStrings theColumnComparison, ComparisonTwoListsOfStrings theTableComparison,
      WhereComparison theWhereComparison, ComparisonTwoListsOfStrings theOrderByComparison,
      ComparisonTwoListsOfStrings theGroupByComparison, SqlExecutionResult theExecutionResult) {
    columnComparison = theColumnComparison;
    tableComparison = theTableComparison;
    whereComparison = theWhereComparison;
    orderByComparison = theOrderByComparison;
    groupByComparison = theGroupByComparison;
    executionResult = theExecutionResult;
  }
  
  public ComparisonTwoListsOfStrings getColumnComparison() {
    return columnComparison;
  }
  
  public SqlExecutionResult getExecutionResult() {
    return executionResult;
  }
  
  public ComparisonTwoListsOfStrings getGroupByComparison() {
    return groupByComparison;
  }
  
  public List<ComparisonTwoListsOfStrings> getListComparisons() {
    ComparisonTwoListsOfStrings[] arr = {columnComparison, tableComparison, orderByComparison, groupByComparison};
    return Arrays.stream(arr).filter(Objects::nonNull).collect(Collectors.toList());
  }

  public ComparisonTwoListsOfStrings getOrderByComparison() {
    return orderByComparison;
  }
  
  public List<? extends EvaluationResult> getResults() {
    // TODO Auto-generated method stub
    return Arrays.asList(columnComparison, tableComparison, whereComparison, orderByComparison, groupByComparison,
        executionResult);
  }
  
  public ComparisonTwoListsOfStrings getTableComparison() {
    return tableComparison;
  }
  
  public WhereComparison getWhereComparison() {
    return whereComparison;
  }
  
}
