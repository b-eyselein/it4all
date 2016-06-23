package model;

import model.exercise.Success;
import model.exercise.EvaluationResult;

public class SqlCorrectionResult extends EvaluationResult {
  
  private String message;
  
  public SqlCorrectionResult(Success theSuccess, String theMessage) {
    super(theSuccess);
    message = theMessage;
  }

  public String getMessage() {
    return message;
  }

}