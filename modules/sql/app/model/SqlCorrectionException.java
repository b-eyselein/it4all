package model;

import model.correction.CorrectionException;

public class SqlCorrectionException extends CorrectionException {

  private static final long serialVersionUID = 3797086667659792252L;

  public SqlCorrectionException(String theLearnerSolution, String msg) {
    super(theLearnerSolution, msg);
  }

  public SqlCorrectionException(String theLearnerSolution, String msg, Throwable cause) {
    super(theLearnerSolution, msg, cause);
  }

}
