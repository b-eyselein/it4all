package model.html.task;

import static model.exercise.EvaluationResult.allResultsSuccessful;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.result.AttributeResult;
import model.html.result.ElementResult;

@Entity
@DiscriminatorValue("css")
public class CssTask extends WebTask {
  
  public static final Finder<TaskKey, CssTask> finder = new Finder<>(CssTask.class);
  
  public CssTask(TaskKey theKey) {
    super(theKey);
  }
  
  @Override
  public EvaluationResult evaluate(SearchContext searchContext) {
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));
    
    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE, "Element konnte nicht gefunden werden!");
    
    if(foundElements.size() > 1)
      return new ElementResult(this, Success.NONE,
          "Element konnte nicht eindeutig identifiziert werden. Existiert das Element eventuell mehrfach?");
    
    WebElement foundElement = foundElements.get(0);

    List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(foundElement);
    Success success;
    if(allResultsSuccessful(evaluatedAttributeResults))
      success = Success.COMPLETE;
    else
      success = Success.PARTIALLY;
    
    return new ElementResult(this, success).withAttributeResults(evaluatedAttributeResults);
    
  }
  
}
