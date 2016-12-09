package model.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.AttributeResult;
import model.result.ElementResult;
import model.result.EvaluationResult;
import model.result.GenericEvaluationResult;

@Entity
public class HtmlTask extends Task {

  public static final Finder<TaskKey, HtmlTask> finder = new Finder<>(HtmlTask.class);

  public String textContent; // NOSONAR

  public HtmlTask(TaskKey theKey) {
    super(theKey);
  }

  private static Success evaluationCorrect(List<AttributeResult> attributeResults, EvaluationResult textResult) {
    if(allResultsSuccessful(attributeResults) && (textResult == null || textResult.getSuccess() == Success.COMPLETE))
      return Success.COMPLETE;
    return Success.PARTIALLY;
  }

  @Override
  public EvaluationResult evaluate(SearchContext searchContext) {
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));

    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE, Collections.emptyList(), null,
          "Element konnte nicht gefunden werden!");

    if(foundElements.size() > 1)
      return new ElementResult(this, Success.NONE, Collections.emptyList(), null,
          "Element konnte nicht eindeutig identifiziert werden. Existiert das Element eventuell mehrfach?");

    // only one matching element left
    WebElement foundElement = foundElements.get(0);

    // Check all attributes of element
    List<AttributeResult> attributeResults = evaluateAllAttributeResults(foundElement);

    // Check text content of element
    EvaluationResult textResult = (textContent == null) ? null : checkTextContent(foundElement.getText());

    return new ElementResult(this, evaluationCorrect(attributeResults, textResult), attributeResults, textResult);
  }

  private EvaluationResult checkTextContent(String foundText) {
    // FIXME: GenericEvaluationResult renders a div with width...
    boolean found = foundText.equals(textContent);
    String messageText = "Textinhalt \"" + foundText + "\" stimmt" + (found ? " nicht" : "")
        + " mit gewünschtem Inhalt \"" + textContent + "\" überein.";
    return new GenericEvaluationResult(FeedbackLevel.MINIMAL_FEEDBACK, found ? Success.COMPLETE : Success.NONE,
        messageText);
  }

}
