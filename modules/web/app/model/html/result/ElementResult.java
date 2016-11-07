package model.html.result;

import java.util.Collections;
import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.html.task.Task;

public class ElementResult extends EvaluationResult {

  private Task task;
  private List<AttributeResult> attributeResults = Collections.emptyList();
  private List<String> parentsMissing;

  public ElementResult(Task theTask, Success theSuccess, String... theMessages) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess, theMessages);
    task = theTask;
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
  
    // Parent elements
    if(parentsMissing != null)
      builder
          .append("<div class=\"alert alert-danger\">Element hat nicht die richtigen Elternelemente. Folgende fehlen: "
              + String.join(", ", parentsMissing) + DIV_END);
    
    // Attribute Results
    for(AttributeResult attResult: attributeResults)
      builder.append(attResult.getAsHtml());
    
    builder.append(DIV_END + DIV_END + DIV_END);
    
    return builder.toString();
  }

  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }

  public List<String> getParentsMissing() {
    return parentsMissing;
  }

  public Task getTask() {
    return task;
  }

  public ElementResult withAttributeResults(List<AttributeResult> theAttributeResults) {
    attributeResults = theAttributeResults;
    return this;
  }

  public ElementResult withParentsMissing(List<String> theParentsMissing) {
    parentsMissing = theParentsMissing;
    return this;
  }

}
