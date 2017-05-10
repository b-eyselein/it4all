package model.result;

import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.task.Task;

public abstract class WebResult extends EvaluationResult {

  protected Task<?> task;

  public WebResult(Task<?> theTask, Success theSuccess, List<String> theMessages) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess, theMessages);
    task = theTask;
  }
  
  public Task<?> getTask() {
    return task;
  }

}
