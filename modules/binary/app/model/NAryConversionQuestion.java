package model;

import java.util.Random;

public class NAryConversionQuestion {
  private static final Random GENERATOR = new Random();

  /**
   * The number that will be converted.
   */
  private NAryNumber startingNumber;
  
  /**
   * The base of the number that will be converted.
   */
  private NumberBase startingNumberBase;

  /**
   * The number, that the startingNumber will be converted to.
   */
  private NAryNumber targetNumber;

  /**
   * The base of the number, that the startingNumber will be converted to.
   */
  private NumberBase targetNumberBase;

  /**
   * Optional learner solution submitted by the user.
   */
  private NAryNumber learnerSolution;

  /**
   * Standard constructor.
   *
   * @param value
   *          The value of the number that will be converted.
   * @param theStartingNB
   *          The base of the number that will be converted.
   * @param theTargetNB
   *          The base that the startingNumber will be converted to.
   */
  public NAryConversionQuestion(int value, NumberBase theStartingNB, NumberBase theTargetNB) {
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;

    startingNumber = new NAryNumber(value, theStartingNB);

    targetNumber = new NAryNumber(value, theTargetNB);

    learnerSolution = new NAryNumber(theTargetNB);
  }

  /**
   * Constructor to use HTML form information (strings).
   *
   * @param value
   * @param theStartingNB
   *          String representation of the base of the number that will be
   *          converted.
   * @param theTargetNB
   *          String representation of the base that the startingNumber will be
   *          converted to.
   * @param theLearnerSolution
   *          String input of the learner.
   */
  public NAryConversionQuestion(String value, NumberBase theStartingNB, NumberBase theTargetNB,
      String theLearnerSolution) {
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;

    startingNumber = new NAryNumber(value, startingNumberBase);

    targetNumber = new NAryNumber(startingNumber.getValue(), theTargetNB);
    learnerSolution = new NAryNumber(theLearnerSolution, targetNumberBase);
  }

  /**
   * Generates a new conversion question.
   *
   * @return instance of NAryConversionQuestion.
   */
  public static NAryConversionQuestion generateNew() {
    int value = GENERATOR.nextInt(256);

    int from = GENERATOR.nextInt(4);
    int to = GENERATOR.nextInt(4);
    while(to == from)
      to = GENERATOR.nextInt(4);

    return new NAryConversionQuestion(value, NumberBase.values()[from], NumberBase.values()[to]);
  }

  /**
   * Checks the correctness of the learners solution.
   *
   * @return
   */
  public boolean checkSolution() {
    return targetNumber.equ(learnerSolution);
  }

  /**
   * Getter for the base of the number that will be converted.
   *
   * @return the startingNumber's NumberBase.
   */
  public NumberBase getFromNumberType() {
    return startingNumberBase;
  }

  /**
   * Getter for the value of the number, that will be converted.
   *
   * @return the startingNumber's value.
   */
  public NAryNumber getFromValue() {
    return startingNumber;
  }

  /**
   * Getter for the learner solution.
   *
   * @return instance of NAryNumber.
   */
  public NAryNumber getLearnerSolution() {
    return learnerSolution;
  }

  /**
   * Getter for the base that the starting number will be converted to.
   *
   * @return the targetNumber's NumberBase.
   */
  public NumberBase getToNumberType() {
    return targetNumberBase;
  }

  /**
   * Getter for the value that the starting number will be converted to.
   *
   * @return the targetNumber's value.
   */
  public NAryNumber getToValue() {
    return targetNumber;
  }

  @Override
  public String toString() {
    return startingNumber.getValue() + ", " + getFromNumberType() + ", " + getToNumberType();
  }
}
