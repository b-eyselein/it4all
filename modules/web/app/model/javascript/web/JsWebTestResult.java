package model.javascript.web;

import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class JsWebTestResult extends EvaluationResult {

  private JsWebTest test;
  private List<ConditionResult> postconditionResults;
  private List<ConditionResult> preconditionResults;

  public JsWebTestResult(JsWebTest theTest, Success theSuccess, List<ConditionResult> thePreconditionResults,
      List<ConditionResult> thePostconditionResults) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess);
    test = theTest;
    preconditionResults = thePreconditionResults;
    postconditionResults = thePostconditionResults;
  }

  @Override
  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    // TODO: restyling? getAsHtml for JsWebTestReault

    StringBuilder builder = new StringBuilder();

    builder.append("<div class=\"panel panel-" + getBSClass() + "\">");
    
    builder.append("<div class=\"panel-heading\"><h4 class=\"panel-title\"><a data-toggle=\"collapse\" href=\"#col"
        + test.id + "\">Test " + (test.id) + "</a></h4>" + DIV_END);

    builder.append("  <div id=\"col" + test.id + "\" class=\"panel-collapse collapse"
        + (success == Success.COMPLETE ? "" : " in") + "\">");
    builder.append("    <div class=\"panel-body\">");

    for(ConditionResult result: preconditionResults)
      builder.append(result.getAsHtml());

    for(ConditionResult result: postconditionResults)
      builder.append(result.getAsHtml());

    builder.append(DIV_END + DIV_END + DIV_END);
    return builder.toString();
  }

  public void postCondResult(List<ConditionResult> thePostconditionResults) {
    postconditionResults = thePostconditionResults;
  }

  public void preCondResult(List<ConditionResult> thePreconditionResults) {
    preconditionResults = thePreconditionResults;
  }

}
