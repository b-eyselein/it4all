package model;

import play.data.DynamicForm;

public class NAryConvResult extends NAryResult {
  
  public static final String STARTING_NB = "startingNB";
  public static final String TARGET_NB = "targetNB";
  
  private NAryNumber startingNumber;
  
  private NumberBase startingNumberBase;
  
  private NumberBase targetNumberBase;
  
  protected NAryConvResult(NAryNumber value, NumberBase theStartingNB, NumberBase theTargetNB,
      NAryNumber theLearnerSolution) {
    super(new NAryNumber(value.decimalValue, theTargetNB), theLearnerSolution);
    startingNumberBase = theStartingNB;
    targetNumberBase = theTargetNB;
    
    startingNumber = value;
    
  }
  
  public static NAryConvResult parseFromForm(DynamicForm form) {
    NumberBase startingNB = NumberBase.valueOf(form.get(STARTING_NB));
    NumberBase targetNB = NumberBase.valueOf(form.get(TARGET_NB));
    
    NAryNumber value = NAryNumber.parse(form.get(VALUE), startingNB);
    
    String learnerSolutionAsString = form.get(StringConsts.FORM_VALUE).replaceAll("\\s", "");
    NAryNumber learnerSolution = NAryNumber.parse(learnerSolutionAsString, targetNB);
    
    return new NAryConvResult(value, startingNB, targetNB, learnerSolution);
  }
  
  public NumberBase getFromNumberBase() {
    return startingNumberBase;
  }
  
  public NAryNumber getFromValue() {
    return startingNumber;
  }
  
  public NumberBase getToNumberBase() {
    return targetNumberBase;
  }
  
}
