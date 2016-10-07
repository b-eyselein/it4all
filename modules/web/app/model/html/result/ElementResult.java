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
  private List<ChildResult> childResults = Collections.emptyList();
  private List<String> parentsMissing;

  public ElementResult(Task theTask, Success theSuccess) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess);
    task = theTask;
  }

  @Override
  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\" data-toggle=\"collapse\" href=\"#task" + task.key.taskId + "\">"
        + task.key.taskId + ". " + task.taskDescription + "</div>";

    ret += "<div id=\"task" + task.key.taskId + "\" class=\"panel-collapse collapse "
        + (success == Success.COMPLETE ? "" : "in") + "\">";
    ret += "<div class=\"panel-body\">";

    // has element been found
    if(success == Success.COMPLETE || success == Success.PARTIALLY)
      ret += "<div class=\"alert alert-success\">Element wurde gefunden!</div>";
    else
      ret += "<div class=\"alert alert-danger\">Element konnte nicht gefunden werden!</div>";

    // Parent elements
    if(parentsMissing != null)
      ret += "<div class=\"alert alert-danger\">Element hat nicht die richtigen Elternelemente. Folgende fehlen: "
          + String.join(", ", parentsMissing) + "</div>";

    // Attribute Results
    for(AttributeResult attResult: attributeResults)
      ret += attResult.getAsHtml();

    // Child Results
    for(ChildResult childRes: childResults)
      ret += childRes.getAsHtml();

    ret += "</div></div>";

    ret += "</div>";

    return ret;
  }

  public List<AttributeResult> getAttributeResults() {
    return attributeResults;
  }

  public List<ChildResult> getChildResults() {
    return childResults;
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

  public ElementResult withChildResults(List<ChildResult> theChildResults) {
    childResults = theChildResults;
    return this;
  }

  public ElementResult withParentsMissing(List<String> theParentsMissing) {
    parentsMissing = theParentsMissing;
    return this;
  }

}
