package model.result;

import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.task.Task;
import model.result.EvaluationResult;

public class ElementResult extends EvaluationResult {

  private Task task;
  private List<AttributeResult> attributeResults;

  private EvaluationResult textContentResult;

  public ElementResult(Task theTask, Success theSuccess, List<AttributeResult> theAttributeResults,
      EvaluationResult theTextContentResult, String... theMessages) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess, theMessages);
    task = theTask;
    attributeResults = theAttributeResults;
    textContentResult = theTextContentResult;
  }

  @Override
  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"panel panel-" + getBSClass() + "\">");
    builder.append("<div class=\"panel-heading\" data-toggle=\"collapse\" href=\"#task" + task.key.taskId + "\">"
        + task.key.taskId + ". " + task.text + DIV_END);

    builder.append("<div id=\"task" + task.key.taskId + "\" class=\"panel-collapse collapse "
        + (success == Success.COMPLETE ? "" : "in") + "\">");
    builder.append("<div class=\"panel-body\">");

    // has element been found
    if(success == Success.COMPLETE || success == Success.PARTIALLY)
      builder.append("<div class=\"alert alert-success\">Element wurde gefunden!</div>");
    else
      // FIXME: join differently!
      builder.append("<div class=\"alert alert-danger\">" + String.join(", ", messages) + "</div>");

    // Text content
    if(textContentResult != null)
      builder.append(textContentResult.getAsHtml());

    // Attribute Results
    for(AttributeResult attResult: attributeResults)
      builder.append(attResult.getAsHtml());

    builder.append(DIV_END + DIV_END + DIV_END);

    return builder.toString();
  }

  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }

  public Task getTask() {
    return task;
  }
  
}
