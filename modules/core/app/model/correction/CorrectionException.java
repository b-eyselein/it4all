package model.correction;

public abstract class CorrectionException extends Exception {

  private static final long serialVersionUID = -639146851013219568L;

  protected final String learnerSolution;

  public CorrectionException(String theLearnerSolution, String msg) {
    super(msg);
    learnerSolution = theLearnerSolution;
  }

  public CorrectionException(String theLearnerSolution, String msg, Throwable cause) {
    super(msg, cause);
    learnerSolution = theLearnerSolution;
  }

  public String getLearnerSolution() {
    return learnerSolution;
  }

}
