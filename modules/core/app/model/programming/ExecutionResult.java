package model.programming;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class ExecutionResult extends IExecutionResult {

  private String awaitedOutput;
  private String evaluated;
  private String realResult;
  private boolean successful;
  private String output;

  public ExecutionResult(String theAwaitedOutput, Success theSuccess, String theEvaluated, String theRealResult,
      String theOutput) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess);
    awaitedOutput = theAwaitedOutput;
    evaluated = theEvaluated;
    realResult = theRealResult;
    requestedFL = FeedbackLevel.FULL_FEEDBACK;
    successful = success == Success.COMPLETE;
    output = theOutput;
  }

  @Override
  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    String ret = "<div class=\"col-md-6\">";
    ret += "<div class=\"alert alert-" + (successful ? "success" : "danger") + "\">\n";
    ret += "  <p>Test von <code>" + evaluated + "</code> war " + (successful ? "" : "nicht ") + "erfolgreich.<p>\n";
    if(!successful && minimalFL.compareTo(this.requestedFL) < 1)
      ret += "  <p>Erwartet: \"" + awaitedOutput + "\", bekommen: \"" + realResult + "\"</p>\n";
    ret += "</div></div>";

    return ret;
  }

  public String getAwaitedResult() {
    return awaitedOutput;
  }

  public String getEvaluated() {
    return evaluated;
  }

  @Override
  public String getOutput() {
    return output;
  }

  @Override
  public Object getResult() {
    return realResult;
  }

  public boolean wasSuccessful() {
    return successful;
  }

}
