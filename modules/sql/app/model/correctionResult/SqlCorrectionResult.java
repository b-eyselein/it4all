package model.correctionResult;

import model.exercise.Success;
import model.SqlQueryResult;
import model.exercise.EvaluationResult;

public class SqlCorrectionResult extends EvaluationResult {

  private String message;
  
  private UsedTablesComparison tableComparisonResult = null;
  private UsedColumnsComparison usedColumnsComparison = null;
  
  private SqlQueryResult userResult = null;
  private SqlQueryResult sampleResult = null;

  public SqlCorrectionResult(Success theSuccess, String theMessage) {
    super(theSuccess);
    message = theMessage;
  }
  
  public String getMessage() {
    return message;
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
  
  public SqlCorrectionResult withExecutionResult(SqlQueryResult theUserResult, SqlQueryResult theSampleResult) {
    userResult = theUserResult;
    sampleResult = theSampleResult;
    return this;
  }
  
  public SqlCorrectionResult withColumnsComparisonResult(UsedColumnsComparison theUsedColumnsComparison) {
    usedColumnsComparison = theUsedColumnsComparison;
    return this;
  }

  public SqlCorrectionResult withTableComparisonResult(UsedTablesComparison theTableComparisonResult) {
    tableComparisonResult = theTableComparisonResult;
    return this;
  }
  
}