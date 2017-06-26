package model;

import play.data.DynamicForm;

public class NAryConvResult extends NAryResult {

  public static final String STARTING_NB = "startingNB";
  public static final String TARGET_NB = "targetNB";
  public static final String VALUE = "value";

  private NAryNumber startingNumber;

  private NumberBase startingNumberBase;

  private NumberBase targetNumberBase;

  private NAryNumber learnerSolution;

  public NAryConvResult(NAryNumber value, NumberBase theStartingNB, NumberBase theTargetNB, String theLearnerSolution) {
    super(new NAryNumber(value.decimalValue, theTargetNB));
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;

    startingNumber = value;

    learnerSolution = NAryNumber.parse(theLearnerSolution, targetNumberBase);
  }

  public static NAryConvResult parseFromForm(DynamicForm form) {
    NumberBase startingNB = NumberBase.valueOf(form.get(STARTING_NB));
    NumberBase targetNB = NumberBase.valueOf(form.get(TARGET_NB));

    NAryNumber value = NAryNumber.parse(form.get(VALUE), startingNB);

    String learnerSolution = form.get(StringConsts.FORM_VALUE).replaceAll("\\s", "");

    return new NAryConvResult(value, startingNB, targetNB, learnerSolution);
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

}
