package model.html;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MultiElementResultByName extends ElementResult {
  
  private List<String> defining_Attributes = new LinkedList<String>();
  private String elemName;
  private HashMap<String, WebElement> singleResults;
  
  public MultiElementResultByName(Task task, String tagName, String elementName, String commonAttributes,
      String differentAttributes) {
    super(task, tagName, commonAttributes);
    elemName = elementName;
    
    for(String attribute: differentAttributes.split(";"))
      if(attribute.contains("="))
        defining_Attributes.add(attribute);
    
    // TODO: Defining Attributes must not be empty (or size >= 2?
    // [___MULTI___ElementResult])
    if(defining_Attributes.size() < 2)
      throw new IllegalArgumentException("Es müssen mindestens 2 definierende Attribute vorhanden sein!");
    
    singleResults = new HashMap<String, WebElement>();
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(elemName));
    if(foundElements.isEmpty()) {
      setResult(Success.NONE, "Es wurde kein Element mit dem Namen \"" + elemName + "\" gefunden");
      return;
    }
    
    foundElements = filterForTagName(foundElements, tag);
    if(foundElements.isEmpty()) {
      setResult(Success.NONE, "Keines der gefundenen Elemente hat den passenden Tag \"" + tag + "\"!");
      return;
    }
    
    // TODO: Stelle sicher, dass alle gefundenen Elemente alle Common-Attribute
    // besitzen
    for(WebElement element: foundElements)
      checkAttributes(element);
    
    // Stelle sicher, dass alle Elemente jeweils ein Different-Attribut
    // enthalten
    for(String attribute: defining_Attributes) {
      for(WebElement element: foundElements) {
        String key = attribute.split("=")[0], value = attribute.split("=")[1];
        AttributeResult attributeResult = new AttributeResult(element, key, value);
        if(attributeResult.isFound()) {
          attrs.add(new AttributeResult(element, key, value));
          singleResults.put(attribute, element);
        }
      }
    }
    
    if(singleResults.size() == defining_Attributes.size())
      setResult(Success.COMPLETE, "TODO");
    else if(singleResults.size() > 0)
      setResult(Success.PARTIALLY, "TODO");
    else
      setResult(Success.NONE, "TODO");
    
  }
  
}
