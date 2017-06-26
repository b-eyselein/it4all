package model;

import play.data.DynamicForm;

public class NAryAddResult extends NAryResult {

  public static final String SUMMAND_1 = "summand1";
  public static final String SUMMAND_2 = "summand2";
  public static final String BASE = "base";

  private NAryNumber firstSummand;
  private NAryNumber secondSummand;

  private NumberBase questionType;

  private NAryNumber learnerSolution;

  private NAryAddResult(NumberBase base, NAryNumber firstSummandInNAry, NAryNumber secondSummandInNAry,
      NAryNumber theLearnerSolution) {
    super(NAryNumber.addNArys(firstSummandInNAry, secondSummandInNAry));
    questionType = base;

    firstSummand = firstSummandInNAry;
    secondSummand = secondSummandInNAry;

    learnerSolution = theLearnerSolution;
  }

  public static NAryAddResult parseFromForm(DynamicForm form) {
    NumberBase base = NumberBase.valueOf(form.get(BASE));

    NAryNumber firstSummand = NAryNumber.parse(form.get(SUMMAND_1), base);
    NAryNumber secondSummand = NAryNumber.parse(form.get(SUMMAND_2), base);

    // Replace all spaces, reverse to compensate input from right to left!
    String learnerSolInNAry = new StringBuilder(form.get(StringConsts.FORM_VALUE)).reverse().toString()
        .replaceAll("\\s", "");
    NAryNumber learnerSol = NAryNumber.parse(learnerSolInNAry, base);

    return new NAryAddResult(base, firstSummand, secondSummand, learnerSol);
  }

  public boolean checkSolution() {
    return targetNumber.decimalValue == learnerSolution.decimalValue;
  }

  public int getBase() {
    return questionType.getBase();
  }

  public NAryNumber getFirstSummand() {
    return firstSummand;
  }

  public NAryNumber getLearnerSolution() {
    return learnerSolution;
  }

  public NumberBase getQuestionType() {
    return questionType;
  }

  public NAryNumber getSecondSummand() {
    return secondSummand;
  }

}
