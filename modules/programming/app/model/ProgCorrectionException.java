package model;

import model.correction.CorrectionException;

public class ProgCorrectionException extends CorrectionException {

  private static final long serialVersionUID = 68835141L;
  
  public ProgCorrectionException(String theLearnerSolution, String msg) {
    super(theLearnerSolution, msg);
  }
  
  public ProgCorrectionException(String learnerSolution, String msg, Throwable cause) {
    super(learnerSolution, msg, cause);
  }
  
}
