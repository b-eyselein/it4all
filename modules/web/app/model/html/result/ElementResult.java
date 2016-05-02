package model.html.result;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.html.task.Task;

public class ElementResult {
  
  protected Task task;
  protected Success success = Success.NONE;
  protected List<AttributeResult> attributeResults;
  protected List<ChildResult> childResults = new LinkedList<ChildResult>();
  protected String reason;

  public ElementResult(Task theTask, Success theSuccess, List<AttributeResult> theAttributeResults,
      List<ChildResult> theChildResults, String theReason) {
    task = theTask;
    success = theSuccess;
    attributeResults = theAttributeResults;
    childResults = theChildResults;
    reason = theReason;
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

  public String getReason() {
    return reason;
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
