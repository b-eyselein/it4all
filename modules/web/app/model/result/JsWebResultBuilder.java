package model.result;

import java.util.LinkedList;
import java.util.List;

import model.task.JsWebTask;

public class JsWebResultBuilder {

  private JsWebTask task;

  private boolean actionPerformed = false;

  private List<ConditionResult> preResults = new LinkedList<>();
  private List<ConditionResult> postResults = new LinkedList<>();

  private List<String> messages = new LinkedList<>();

  public JsWebResultBuilder(JsWebTask theTask) {
    task = theTask;
  }

  public JsWebResult build() {
    return new JsWebResult(task, preResults, actionPerformed, postResults, messages);
  }

  public JsWebResultBuilder withActionPerformed(boolean theActionPerformed) {
    actionPerformed = theActionPerformed;
    return this;
  }

  public JsWebResultBuilder withMessage(String message) {
    messages.add(message);
    return this;
  }

  public JsWebResultBuilder withPostResults(List<ConditionResult> thePostResults) {
    postResults = thePostResults;
    return this;
  }

  public JsWebResultBuilder withPreResults(List<ConditionResult> thePreResults) {
    preResults = thePreResults;
    return this;
  }

}
