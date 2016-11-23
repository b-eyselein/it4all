package model.result;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class JsTestResult extends EvaluationResult {
  
  private String awaitedOutput;
  private String evaluated;
  private String realResult;
  private boolean successful;
  
  public JsTestResult(String theAwaitedOutput, Success theSuccess, String theEvaluated, String theRealResult) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess);
    awaitedOutput = theAwaitedOutput;
    evaluated = theEvaluated;
    realResult = theRealResult;
    requestedFL = FeedbackLevel.FULL_FEEDBACK;
    successful = success == Success.COMPLETE;
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
  
  public String getRealResult() {
    return realResult;
  }
  
  public boolean wasSuccessful() {
    return successful;
  }
}
