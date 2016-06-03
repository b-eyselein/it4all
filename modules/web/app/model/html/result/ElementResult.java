package model.html.result;

import java.util.Collections;
import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.task.Task;

public class ElementResult extends EvaluationResult {
  
  private Task task;
  private List<AttributeResult> attributeResults = Collections.emptyList();
  private List<ChildResult> childResults = Collections.emptyList();

  public ElementResult(Task theTask, Success theSuccess) {
    task = theTask;
    success = theSuccess;
  }

  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }

  public List<ChildResult> getChildResults() {
    return childResults;
  }

  public Task getTask() {
    return task;
  }

  public ElementResult withAttributeResults(List<AttributeResult> theAttributeResults) {
    attributeResults = theAttributeResults;
    return this;
  }

  public ElementResult withChildResults(List<ChildResult> theChildResults) {
    childResults = theChildResults;
    return this;
  }

}
