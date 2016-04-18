package model.html.result;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.html.task.Task;

public abstract class ElementResult<TaskType extends Task> {

  protected TaskType task;
  protected Success success = Success.NONE;
  protected List<AttributeResult> attributeResults;

  // Bisher nur bei NameResult!
  protected List<ChildResult> childResults = new LinkedList<ChildResult>();

  public ElementResult(TaskType theTask, Success theSuccess, List<AttributeResult> theAttributeResults) {
    task = theTask;
    success = theSuccess;
    attributeResults = theAttributeResults;
  }

  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }

  public List<ChildResult> getChildResults() {
    return childResults;
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
