package model.html.result;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.html.task.Task;

public abstract class ElementResult<TaskType extends Task> {

  protected TaskType task;
  protected Success success = Success.NONE;
  protected List<AttributeResult> attributeResults;

  public ElementResult(TaskType theTask, Success theSuccess, List<AttributeResult> theAttributeResults) {
    task = theTask;
    success = theSuccess;
    attributeResults = theAttributeResults;
  }

  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }

  @JsonIgnore
  public int getPoints() {
    if(success == Success.NONE)
      return 0;
    else if(success == Success.COMPLETE)
      return 2;
    else
      return 1;
  }

  public Success getSuccess() {
    return success;
  }

  public Task getTask() {
    return task;
  }

  public void setResult(Success suc) {
    success = suc;
  }

}
