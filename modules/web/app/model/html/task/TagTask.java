package model.html.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.openqa.selenium.WebElement;

import model.html.result.AttributeResult;
import model.html.result.ElementResult;
import model.html.result.Success;

@Entity
@DiscriminatorValue("tag")
public class TagTask extends Task {
  
  @Override
  public ElementResult evaluateMore(List<WebElement> foundElements) {
    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList());
    
    for(WebElement element: foundElements) {
      List<AttributeResult> attributeResults = evaluateAllAttributes(element);
      if(allAttributesFound(attributeResults))
        return new ElementResult(this, Success.COMPLETE, attributeResults, Collections.emptyList());
    }
    
    return new ElementResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList());
  }
}
