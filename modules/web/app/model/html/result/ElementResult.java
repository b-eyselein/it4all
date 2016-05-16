package model.html.result;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.task.Task;

public class ElementResult extends EvaluationResult {
  
  private Task task;
  private List<AttributeResult> attributeResults;
  private List<ChildResult> childResults;

  public ElementResult(Task theTask, Success theSuccess, List<AttributeResult> theAttributeResults,
      List<ChildResult> theChildResults) {
    task = theTask;
    success = theSuccess;
    attributeResults = theAttributeResults;
    childResults = theChildResults;
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

}
