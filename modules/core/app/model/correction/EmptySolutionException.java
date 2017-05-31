package model.correction;

public class EmptySolutionException extends CorrectionException {

  private static final long serialVersionUID = 654351354654165L;

  public EmptySolutionException(String theLearnerSolution, String msg) {
    super(theLearnerSolution, msg);
  }

}
