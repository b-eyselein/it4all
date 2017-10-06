package model.result;

import java.util.List;

import model.exercise.Success;
import model.task.WebTask;

public abstract class WebResult extends EvaluationResult {

  protected WebTask task;

  // FIXME: are there really messages?
  protected List<String> messages;

  public WebResult(WebTask theTask, Success theSuccess, List<String> theMessages) {
    super(theSuccess);
    task = theTask;
    messages = theMessages;
  }

  public List<String> getMessages() {
    return messages;
  }

  public WebTask getTask() {
    return task;
  }

}
