package model.javascript.web;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class ConditionResult extends EvaluationResult {
  
  private Condition condition;
  private boolean isPre;
  private String gottenValue;
  
  public ConditionResult(Success theSuccess, Condition theCondition, String theGottenValue, boolean isPrecondition) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess);
    condition = theCondition;
    gottenValue = theGottenValue;
    isPre = isPrecondition;
  }
  
  @Override
  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    String ret = "<div class=\"alert alert-" + getBSClass() + "\">";
    
    ret += "<p>" + (isPre ? "Vor" : "Nach") + "bedingung konnte " + (success == Success.COMPLETE ? "" : "nicht ")
        + "verifiziert werden.</p>";
    
    ret += "<p>" + condition.getDescription() + "</p>";
    
    if(success != Success.COMPLETE)
      ret += "<p>Element hatte aber folgenden Wert: " + gottenValue + "</p>";
    
    ret += "</div>";
    return ret;
  }
  
}
