package model.html.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.openqa.selenium.WebElement;

import model.html.result.AttributeResult;
import model.html.result.ChildResult;
import model.html.result.ElementResult;
import model.html.result.Success;
import play.Logger;

@Entity
@DiscriminatorValue("name")
public class NameTask extends Task {
  
  @Override
  public ElementResult evaluateMore(List<WebElement> foundElements) {
    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList());
    
    if(foundElements.size() > 1)
      Logger.warn("Mehrere Elemente gefunden, benutze erstes!");
    
    WebElement element = foundElements.get(0);
    
    // FIXME: ChildResults!
    List<ChildResult> childResults = new LinkedList<ChildResult>();
    childTasks.stream().map(childTask -> childTask.getChildResult()).forEach(childResult -> {
      childResult.evaluate(element);
      childResults.add(childResult);
    });
    
    List<AttributeResult> attributeResults = evaluateAllAttributes(element);
    
    if(allAttributesFound(attributeResults) && allChildElementsFound(childResults))
      return new ElementResult(this, Success.COMPLETE, attributeResults, childResults);
    else
      return new ElementResult(this, Success.PARTIALLY, attributeResults, childResults);
  }
  
}
