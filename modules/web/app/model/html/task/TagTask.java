package model.html.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.html.result.AttributeResult;
import model.html.result.ElementResult;
import model.html.result.Success;
import model.html.result.TagResult;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

@Entity
@DiscriminatorValue("tag")
public class TagTask extends Task {
  
  @Override
  public ElementResult<? extends Task> evaluate(SearchContext searchContext) {
    List<WebElement> foundElements = searchContext.findElements(By.tagName(tagName));
    if(foundElements.isEmpty())
      return new TagResult(this, Success.NONE, Collections.emptyList());
    
    // TODO: Mehrere Elemente!
    // if(foundElements.size() > 1)
    // message = "Es wurde mehr als 1 Element mit passendem Namen" +
    // " und passendem Tag gefunden. Verwende das erste für weitere Korrektur. ";
    WebElement element = foundElements.get(0);
    
    List<AttributeResult> attributeResults = evaluateAllAttributes(element);
    
    if(allAttributesFound(attributeResults))
      return new TagResult(this, Success.COMPLETE, attributeResults);
    else
      return new TagResult(this, Success.PARTIALLY, attributeResults);
  }
  
}
