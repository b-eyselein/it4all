package model.result;

import java.util.List;

import model.exercise.Success;
import model.task.WebTask;

public abstract class WebResult extends EvaluationResult {

  protected WebTask task;

  public WebResult(WebTask theTask, Success theSuccess, List<String> theMessages) {
    super(theSuccess, theMessages);
    task = theTask;
  }

  public WebTask getTask() {
    return task;
  }

}
