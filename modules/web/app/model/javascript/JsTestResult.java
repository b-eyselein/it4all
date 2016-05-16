package model.javascript;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class JsTestResult extends EvaluationResult {
  
  private JsTest test;
  private String evaluated;
  private String realResult;

  public JsTestResult(JsTest theTest, Success theSuccess, String theEvaluated, String theRealResult) {
    test = theTest;
    success = theSuccess;
    evaluated = theEvaluated;
    realResult = theRealResult;
  }

  // public String getAsString() {
  // // FIXME: Format festlegen und implementieren!
  // String ret = wasSuccessful() ? "+ " : "- ";
  // ret += "Test " + test.id + ": \"" + evaluated + "\" ";
  // ret += wasSuccessful() ? "war erfolgreich." : "war nicht erfolgreich";
  // return ret;
  // }

  public String getAwaitedResult() {
    return test.awaitedResult;
  }

  public String getEvaluated() {
    return evaluated;
  }

  public String getRealResult() {
    return realResult;
  }

  public boolean wasSuccessful() {
    return realResult.equals(test.awaitedResult);
  }
}
