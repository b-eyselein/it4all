package model.html.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.openqa.selenium.WebElement;

import model.html.result.ElementResult;
import model.html.result.Success;

@Entity
@DiscriminatorValue("multiname")
public class MultiNameTask extends Task {
  
  @Column(name = "elemName")
  public String elemName;
  
  @Override
  public ElementResult evaluateMore(List<WebElement> foundElements) {
    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList());
    
    // TODO: Stelle sicher, dass alle gefundenen Elemente alle Common-Attribute
    // besitzen
    for(WebElement element: foundElements)
      evaluateAllAttributes(element);
    
    // Stelle sicher, dass alle Elemente jeweils ein Different-Attribut
    // enthalten
    // for(String attribute: defining_Attributes) {
    // for(WebElement element: foundElements) {
    // String key = attribute.split("=")[0], value = attribute.split("=")[1];
    // AttributeResult attributeResult = new AttributeResult(element, key,
    // value);
    // if(attributeResult.isFound()) {
    // attrs.add(new AttributeResult(element, key, value));
    // singleResults.put(attribute, element);
    // }
    // }
    // }
    
    // if(singleResults.size() == defining_Attributes.size())
    // return Success.COMPLETE;
    // else if(singleResults.size() > 0)
    // return Success.PARTIALLY;
    // else
    
    return new ElementResult(this, Success.PARTIALLY, Collections.emptyList(), Collections.emptyList());
    
  }
  
}
