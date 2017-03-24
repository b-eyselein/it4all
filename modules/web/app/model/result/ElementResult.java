package model.result;

import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.task.Task;

public class ElementResult extends EvaluationResult {

  protected Task task;
  protected List<AttributeResult> attributeResults;

  protected EvaluationResult textContentResult;

  public ElementResult(Task theTask, Success theSuccess, List<AttributeResult> theAttributeResults,
      EvaluationResult theTextContentResult, String... theMessages) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess, theMessages);
    task = theTask;
    attributeResults = theAttributeResults;
    textContentResult = theTextContentResult;
  }

  @Override
  public String getAsHtml() {
    throw new IllegalArgumentException("Cannot be used anymore!");
  }

  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }

  public Task getTask() {
    return task;
  }

  public EvaluationResult getTextContentResult() {
    return textContentResult;
  }

}
