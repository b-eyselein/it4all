package model;

import play.Logger;

public abstract class NAryResult {
  
  public static final String VALUE = "value";

  protected NAryNumber targetNumber;
  protected NAryNumber learnerSolution;

  public NAryResult(NAryNumber theTargetNumber, NAryNumber theLearnerSolution) {
    targetNumber = theTargetNumber;
    learnerSolution = theLearnerSolution;
  }

  public boolean checkSolution() {
    Logger.debug(targetNumber.getValue() + " :: " + learnerSolution.getValue());
    return targetNumber.getValue() == learnerSolution.getValue();
  }

  public NAryNumber getLearnerSolution() {
    return learnerSolution;
  }

  public NAryNumber getTargetNumber() {
    return targetNumber;
  }

}
