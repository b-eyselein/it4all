package model;

public abstract class NAryResult {

  protected NAryNumber targetNumber;

  public NAryResult(NAryNumber theTargetNumber) {
    targetNumber = theTargetNumber;
  }

  public abstract boolean checkSolution();

  public NAryNumber getTargetNumber() {
    return targetNumber;
  }
}
