package model.html.result;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.html.task.JsCondition;

public class JsConditionResult extends EvaluationResult {

  private JsCondition condition;
  private boolean isPre;
  private String gottenValue;

  public JsConditionResult(Success theSuccess, JsCondition theCondition, String theGottenValue,
      boolean isPrecondition) {
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
