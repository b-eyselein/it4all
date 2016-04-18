package model.html.result;

import java.util.List;

import model.html.task.NameTask;

public class NameResult extends ElementResult<NameTask> {
  
  public NameResult(NameTask task, Success success, List<AttributeResult> attributeResults,
      List<ChildResult> theChildResults) {
    super(task, success, attributeResults);
    childResults = theChildResults;
  }
  
}
