package model;

import play.data.DynamicForm;

public class TwoCompResult extends NAryResult {

  public static final String BINARY_ABS = "binaryAbs";
  public static final String INVERTED_ABS = "inverted";

  private static final String ZERO = "0";
  
  private String binaryAbs;
  private String invertedAbs;

  public TwoCompResult(NAryNumber theTargetNumber, NAryNumber theLearnerSolution, String theBinaryAbs,
      String theInverted) {
    super(theTargetNumber, theLearnerSolution);
    binaryAbs = NAryNumber.padBinary(theBinaryAbs);
    invertedAbs = NAryNumber.padBinary(theInverted);
  }

  public static TwoCompResult parseFromForm(DynamicForm form, boolean isVerbose) {
    NAryNumber targetNumber = new NAryNumber(Integer.parseInt(form.get(NAryResult.VALUE)), NumberBase.BINARY);
    NAryNumber learnerSolution = NAryNumber.parseTwoComplement(form.get(StringConsts.FORM_VALUE));

    String binaryAbs = isVerbose ? form.get(TwoCompResult.BINARY_ABS) : ZERO;
    String invertedAbs = isVerbose ? form.get(TwoCompResult.INVERTED_ABS) : ZERO;

    return new TwoCompResult(targetNumber, learnerSolution, binaryAbs, invertedAbs);
  }

  public boolean binaryAbsCorrect() {
    int binaryAbsValue = NAryNumber.parse(binaryAbs, NumberBase.BINARY).getValue();
    return binaryAbsValue == Math.abs(targetNumber.getValue());
  }

  public String getBinaryAbs() {
    return binaryAbs;
  }

  public String getInvertedAbs() {
    return invertedAbs;
  }

  public boolean invertedAbsCorrect() {
    return NAryNumber.invertDigits(binaryAbs).equals(invertedAbs);
  }

}