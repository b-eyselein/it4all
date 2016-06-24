package model;

import model.exercise.Success;
import model.exercise.EvaluationResult;

public class SqlCorrectionResult extends EvaluationResult {

  private String message;
  private TableComparisonResult tableComparisonResult;

  public SqlCorrectionResult(Success theSuccess, String theMessage) {
    super(theSuccess);
    message = theMessage;
  }
  
  public String getMessage() {
    return message;
  }

  public TableComparisonResult getTableComparisonResult() {
    return tableComparisonResult;
  }

  public SqlCorrectionResult withTableComparisonResult(TableComparisonResult theTableComparisonResult) {
    tableComparisonResult = theTableComparisonResult;
    return this;
  }
  
}