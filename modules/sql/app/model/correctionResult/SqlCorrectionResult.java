package model.correctionResult;

import java.util.LinkedList;
import java.util.List;

import model.SqlQueryResult;
import model.exercise.EvaluationResult;
import model.exercise.Success;

public class SqlCorrectionResult extends EvaluationResult {

  private List<String> messages = new LinkedList<>();
  
  private UsedTablesComparison tableComparisonResult = null;
  private UsedColumnsComparison usedColumnsComparison = null;
  
  private SqlQueryResult userResult = null;
  private SqlQueryResult sampleResult = null;

  public SqlCorrectionResult(Success theSuccess, List<String> theMessages) {
    super(theSuccess);
    messages.addAll(theMessages);
  }
  
  public SqlCorrectionResult(Success theSuccess, String aMessage) {
    super(theSuccess);
    messages.add(aMessage);
  }
  
  public List<String> getMessages() {
    return messages;
  }

  public SqlQueryResult getSampleResult() {
    return sampleResult;
  }

  public UsedColumnsComparison getUsedColumnsComparison() {
    return usedColumnsComparison;
  }

  public UsedTablesComparison getUsedTablesComparison() {
    return tableComparisonResult;
  }

  public SqlQueryResult getUserResult() {
    return userResult;
  }
  
  public SqlCorrectionResult withColumnsComparisonResult(UsedColumnsComparison theUsedColumnsComparison) {
    usedColumnsComparison = theUsedColumnsComparison;
    return this;
  }
  
  public SqlCorrectionResult withExecutionResult(SqlQueryResult theUserResult, SqlQueryResult theSampleResult) {
    userResult = theUserResult;
    sampleResult = theSampleResult;
    return this;
  }

  public SqlCorrectionResult withTableComparisonResult(UsedTablesComparison theTableComparisonResult) {
    tableComparisonResult = theTableComparisonResult;
    return this;
  }
  
}