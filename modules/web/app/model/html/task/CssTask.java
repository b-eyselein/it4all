package model.html.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Finder;

import model.exercise.Success;
import model.html.result.AttributeResult;
import model.html.result.ChildResult;
import model.html.result.ElementResult;

@Entity
public class CssTask extends Task {
  
  public static final Finder<TaskKey, CssTask> finder = new Finder<>(CssTask.class);
  
  public CssTask(TaskKey theKey) {
    super(theKey);
  }
  
  @Override
  public ElementResult evaluate(SearchContext searchContext) {
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));
    
    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE);
    
    boolean allSuccesful = true;
    for(WebElement element: foundElements) {
      List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(element);
      if(!allResultsSuccessful(evaluatedAttributeResults))
        allSuccesful = false;
    }
    
    if(allSuccesful)
      return new ElementResult(this, Success.COMPLETE);
    else
      return new ElementResult(this, Success.PARTIALLY);
  }

  @Override
  protected List<ChildResult> getChildResults() {
    return Collections.emptyList();
  }
  
}
