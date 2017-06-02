package model.correction;

public class JsonValidationException extends CorrectionException {

  private static final long serialVersionUID = 684868146L;

  public JsonValidationException(String theLearnerSolution, String msg) {
    super(theLearnerSolution, msg);
  }

  public JsonValidationException(String theLearnerSolution, String msg, Throwable cause) {
    super(theLearnerSolution, msg, cause);
  }

}
