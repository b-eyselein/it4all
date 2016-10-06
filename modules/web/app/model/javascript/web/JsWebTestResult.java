package model.javascript.web;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class JsWebTestResult extends EvaluationResult {

  private JsWebTest test;
  private List<ConditionResult> postconditionResults;
  private List<ConditionResult> preconditionResults;

  public JsWebTestResult(JsWebTest theTest, Success theSuccess, List<ConditionResult> thePreconditionResults,
      List<ConditionResult> thePostconditionResults) {
    super(theSuccess);
    test = theTest;
    preconditionResults = thePreconditionResults;
    postconditionResults = thePostconditionResults;
  }

  @Override
  public String getAsHtml() {
    // TODO: restyling? getAsHtml for JsWebTestReault
    String toAdd = "<div class=\"panel panel-" + getBSClass() + "\">";
    toAdd += "  <div class=\"panel-heading\">";
    toAdd += "    <h4 class=\"panel-title\"><a data-toggle=\"collapse\" href=\"#col" + test.id + "\">Test " + (test.id)
        + "</a></h4>";
    toAdd += "  </div>";

    toAdd += "  <div id=\"col" + test.id + "\" class=\"panel-collapse collapse"
        + (success == Success.COMPLETE ? "" : " in") + "\">";
    toAdd += "    <div class=\"panel-body\">";

    for(ConditionResult result: preconditionResults)
      toAdd += result.getAsHtml();

    for(ConditionResult result: postconditionResults)
      toAdd += result.getAsHtml();

    toAdd += "    </div>";
    toAdd += "  </div>";
    toAdd += "</div>";
    return toAdd;
  }

  public void postCondResult(List<ConditionResult> thePostconditionResults) {
    postconditionResults = thePostconditionResults;
  }

  public void preCondResult(List<ConditionResult> thePreconditionResults) {
    preconditionResults = thePreconditionResults;
  }

}
