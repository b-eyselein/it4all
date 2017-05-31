package model.result;

import java.util.List;

import model.exercise.Success;
import model.task.Task;

public abstract class WebResult<T extends WebResult<T>> extends EvaluationResult {

  protected Task<T> task;

  public WebResult(Task<T> theTask, Success theSuccess, List<String> theMessages) {
    super(theSuccess, theMessages);
    task = theTask;
  }

  public Task<T> getTask() {
    return task;
  }

}
