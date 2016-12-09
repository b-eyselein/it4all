package model.javascript;

import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.html.task.JsWebTask;
import model.result.EvaluationResult;

public class JsWebTestResult extends EvaluationResult {

  private JsWebTask test;
  private List<ConditionResult> postconditionResults;
  private List<ConditionResult> preconditionResults;
  private boolean actionPerformed;

  public JsWebTestResult(JsWebTask theTest, List<ConditionResult> thePreResults, boolean theActionPerf,
      List<ConditionResult> thePostResults) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, analyze(thePostResults, theActionPerf, thePostResults));
    test = theTest;
    preconditionResults = thePreResults;
    actionPerformed = theActionPerf;
    postconditionResults = thePostResults;
  }

  public static <T extends EvaluationResult> boolean allResultsSuccessful(List<T> results) {
    for(EvaluationResult res: results)
      if(res.getSuccess() != Success.COMPLETE)
        return false;
    return true;
  }

  public static Success analyze(List<ConditionResult> preconds, boolean actionPerf, List<ConditionResult> postconds) {
    if(allResultsSuccessful(preconds) && actionPerf && allResultsSuccessful(postconds))
      return Success.COMPLETE;
    return Success.NONE;
  }

  @Override
  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    // TODO: restyling? getAsHtml for JsWebTestReault

    StringBuilder builder = new StringBuilder();

    builder.append("<div class=\"panel panel-" + getBSClass() + "\">");

    builder.append("<div class=\"panel-heading\"><h4 class=\"panel-title\"><a data-toggle=\"collapse\" href=\"#col"
        + test.key.taskId + "\">Test " + (test.key.taskId) + "</a></h4>" + DIV_END);

    builder.append("  <div id=\"col" + test.key.taskId + "\" class=\"panel-collapse collapse"
        + (success == Success.COMPLETE ? "" : " in") + "\">");
    builder.append("    <div class=\"panel-body\">");

    for(ConditionResult result: preconditionResults)
      builder.append(result.getAsHtml());

    builder.append("<div class=\"alert alert-" + (actionPerformed ? "success" : "danger") + "\">Aktion konnte "
        + (actionPerformed ? "" : "nicht ") + " erfolgreich ausgeführt werden.</div>");

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
