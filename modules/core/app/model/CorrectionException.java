package model;

public class CorrectionException extends Exception {
  
  private static final long serialVersionUID = -639146851013219568L;
  
  private final String learnerSolution;
  
  public CorrectionException(String theLearnerSolution, String msg) {
    super(msg);
    learnerSolution = theLearnerSolution;
  }
  
  public CorrectionException(String theLearnerSolution, String msg, Throwable cause) {
    super(msg, cause);
    learnerSolution = theLearnerSolution;
  }
  
  public CorrectionException(String msg, Throwable cause) {
    this("", msg, cause);
  }
  
  public String getLearnerSolution() {
    return learnerSolution;
  }
  
}
