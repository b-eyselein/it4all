package model.javascript.web;

import java.util.LinkedList;
import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class JsWebTestResult extends EvaluationResult {

  private JsWebTest test;
  private List<String> messages = new LinkedList<>();

  public JsWebTestResult(JsWebTest theTest, Success theSuccess, List<String> theMessages) {
    super(theSuccess);
    test = theTest;
    messages = theMessages;
  }
  
  public JsWebTestResult(JsWebTest theTest, Success theSuccess, String theMessage) {
    super(theSuccess);
    test = theTest;
    messages.add(theMessage);
  }

  public List<String> getMessages() {
    return messages;
  }

  public JsWebTest getTest() {
    return test;
  }

}
