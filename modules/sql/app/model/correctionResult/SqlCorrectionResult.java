package model.correctionResult;

import java.util.LinkedList;
import java.util.List;

import model.SqlQueryResult;
import model.exercise.EvaluationResult;
import model.exercise.Success;

public class SqlCorrectionResult extends EvaluationResult {

  private List<String> messages = new LinkedList<>();
  
  private TableComparison tableComparison = null;
  private ColumnComparison columnComparison = null;
  
  private SqlQueryResult userResult = null;
  private SqlQueryResult sampleResult = null;

  public SqlCorrectionResult(Success theSuccess) {
    super(theSuccess);
  }
  
  public SqlCorrectionResult(Success theSuccess, List<String> theMessages) {
    super(theSuccess);
    messages.addAll(theMessages);
  }

  public SqlCorrectionResult(Success theSuccess, String aMessage) {
    super(theSuccess);
    messages.add(aMessage);
  }
  
  public SqlCorrectionResult(Success theSuccess, ColumnComparison theColumnComparison, TableComparison theTableComparison) {
    super(theSuccess);
    columnComparison = theColumnComparison;
    tableComparison = theTableComparison;
  }
  
  public List<String> getMessages() {
    return messages;
  }

  public SqlQueryResult getSampleResult() {
    return sampleResult;
  }

  public ColumnComparison getColumnComparison() {
    return columnComparison;
  }

  public TableComparison getTableComparison() {
    return tableComparison;
  }

  public SqlQueryResult getUserResult() {
    return userResult;
  }
  
  public SqlCorrectionResult withColumnsComparisonResult(ColumnComparison theUsedColumnsComparison) {
    columnComparison = theUsedColumnsComparison;
    return this;
  }
  
  public SqlCorrectionResult withExecutionResult(SqlQueryResult theUserResult, SqlQueryResult theSampleResult) {
    userResult = theUserResult;
    sampleResult = theSampleResult;
    return this;
  }

  public SqlCorrectionResult withTableComparisonResult(TableComparison theTableComparisonResult) {
    tableComparison = theTableComparisonResult;
    return this;
  }
  
}