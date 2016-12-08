package model.html.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import model.exercise.Success;
import model.html.result.AttributeResult;
import model.html.result.ElementResult;
import model.result.EvaluationResult;

@Entity
public class CssTask extends Task {

  public static final Finder<TaskKey, CssTask> finder = new Finder<>(CssTask.class);

  public CssTask(TaskKey theKey) {
    super(theKey);
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

    WebElement foundElement = foundElements.get(0);

    List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(foundElement);
    Success success;
    if(allResultsSuccessful(evaluatedAttributeResults))
      success = Success.COMPLETE;
    else
      success = Success.PARTIALLY;

    return new ElementResult(this, success, evaluatedAttributeResults, null);

  }

}
