package model;

import play.data.DynamicForm;

public class TwoCompResult extends NAryResult {

  public static final String BINARY_ABS = "binaryAbs";

  public static final String INVERTED = "inverted";

  public TwoCompResult(NAryNumber theTargetNumber, NAryNumber theLearnerSolution) {
    super(theTargetNumber, theLearnerSolution);
  }

  public static TwoCompResult parseFromForm(DynamicForm form) {
    NAryNumber decNumber = new NAryNumber(Integer.parseInt(form.get(NAryResult.VALUE)), NumberBase.BINARY);
    NAryNumber twoComplement = NAryNumber.parseTwoComplement(form.get(StringConsts.FORM_VALUE));
    return new TwoCompResult(decNumber, twoComplement);
  }

}