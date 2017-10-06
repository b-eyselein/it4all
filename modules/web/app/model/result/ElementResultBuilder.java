package model.result;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.WebElement;

import model.exercise.Success;
import model.task.WebTask;

public class ElementResultBuilder {

  private WebTask task = null;

  private WebElement foundElement = null;

  private TextContentResult textContentResult = null;

  private List<AttributeResult> attributeResults = new LinkedList<>();

  private List<String> messages = new LinkedList<>();

  public ElementResultBuilder(WebTask theTask) {
    task = theTask;
  }

  public ElementResult build() {
    return new ElementResult(task, suanalyzeSuccess(), attributeResults, textContentResult, messages);
  }

  public ElementResultBuilder withAttributeResults(List<AttributeResult> theAttributeResults) {
    attributeResults = theAttributeResults;
    return this;
  }

  public ElementResultBuilder withFoundElement(WebElement theFoundElement) {
    foundElement = theFoundElement;
    return this;
  }

  public ElementResultBuilder withMessage(String message) {
    messages.add(message);
    return this;
  }

  public ElementResultBuilder withTextContentResult(TextContentResult theTextContentResult) {
    textContentResult = theTextContentResult;
    return this;
  }

  private Success suanalyzeSuccess() {
    if(foundElement == null)
      return Success.NONE;

    if(!EvaluationResult.allResultsSuccessful(attributeResults))
      return Success.PARTIALLY;

    if(textContentResult != null && !textContentResult.isSuccessful())
      return Success.PARTIALLY;

    return Success.COMPLETE;
  }

}
