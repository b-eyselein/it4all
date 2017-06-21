package model;

import java.util.Random;

public class NAryConversionQuestion {

  private static final Random GENERATOR = new Random();

  private NAryNumber startingNumber;

  private NumberBase startingNumberBase;

  private NAryNumber targetNumber;

  private NumberBase targetNumberBase;

  private NAryNumber learnerSolution;

  public NAryConversionQuestion(int value, NumberBase theStartingNB, NumberBase theTargetNB) {
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;

    startingNumber = new NAryNumber(value, theStartingNB);

    targetNumber = new NAryNumber(value, theTargetNB);

    learnerSolution = new NAryNumber(theTargetNB);
  }

  public NAryConversionQuestion(String value, NumberBase theStartingNB, NumberBase theTargetNB,
      String theLearnerSolution) {
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;

    startingNumber = NAryNumber.parse(value, startingNumberBase);

    targetNumber = new NAryNumber(startingNumber.getValue(), theTargetNB);
    learnerSolution = NAryNumber.parse(theLearnerSolution, targetNumberBase);
  }

  public static NAryConversionQuestion generateNew() {
    int value = GENERATOR.nextInt(256);

    int from = GENERATOR.nextInt(4);
    int to = GENERATOR.nextInt(4);
    while(to == from)
      to = GENERATOR.nextInt(4);

    return new NAryConversionQuestion(value, NumberBase.values()[from], NumberBase.values()[to]);
  }

  public boolean checkSolution() {
    return targetNumber.equals(learnerSolution);
  }

  public NumberBase getFromNumberType() {
    return startingNumberBase;
  }

  public NAryNumber getFromValue() {
    return startingNumber;
  }

  public NAryNumber getLearnerSolution() {
    return learnerSolution;
  }

  public NumberBase getToNumberType() {
    return targetNumberBase;
  }

  public NAryNumber getToValue() {
    return targetNumber;
  }

  @Override
  public String toString() {
    return startingNumber.getValue() + ", " + getFromNumberType() + ", " + getToNumberType();
  }
}
