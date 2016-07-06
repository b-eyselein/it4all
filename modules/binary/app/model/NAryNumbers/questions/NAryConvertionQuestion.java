package model.NAryNumbers.questions;

import java.util.Random;

import model.NAryNumbers.BinaryNumber;
import model.NAryNumbers.NAryNumber;

public class NAryConvertionQuestion {
  private static final Random GENERATOR = new Random();

  public static NAryConvertionQuestion generateNew() {
    int value = GENERATOR.nextInt(256);

    int from = GENERATOR.nextInt(4);
    int to = GENERATOR.nextInt(4);
    while(to == from)
      to = GENERATOR.nextInt(4);

    return new NAryConvertionQuestion(value, NumberBase.values()[from], NumberBase.values()[to]);
  }

  private NAryNumber startingNumber;
  private NumberBase startingNumberBase;

  private NAryNumber targetNumber;
  private NumberBase targetNumberBase;

  private NAryNumber learnerSolution;

  public NAryConvertionQuestion(int value, NumberBase theStartingNB, NumberBase theTargetNB) {
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;

    if(startingNumberBase == NumberBase.BINARY)
      startingNumber = new BinaryNumber(value);
    else
      startingNumber = new NAryNumber(value, theStartingNB);

    if(targetNumberBase == NumberBase.BINARY)
      targetNumber = new BinaryNumber(value);
    else
      targetNumber = new NAryNumber(value, theTargetNB);

    learnerSolution = new NAryNumber(theTargetNB);
  }

  public NAryConvertionQuestion(String value, NumberBase theStartingNB, NumberBase theTargetNB,
      String theLearnerSolution) {
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;

    if(startingNumberBase == NumberBase.BINARY)
      startingNumber = BinaryNumber.stringToBin(value);
    else
      startingNumber = NAryNumber.stringToNAry(value, startingNumberBase);

    if(targetNumberBase == NumberBase.BINARY)
      targetNumber = new BinaryNumber(startingNumber.getValue());
    else
      targetNumber = new NAryNumber(startingNumber.getValue(), theTargetNB);
    learnerSolution = NAryNumber.stringToNAry(theLearnerSolution, targetNumberBase);
  }

  public boolean checkSolution() {
    return targetNumber.equ(learnerSolution);
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
    return startingNumber.getValue() + "," + getFromNumberType() + "," + getToNumberType();
  }
}
