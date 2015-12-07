package model.html;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.Success;
import model.Task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResultByName extends ElementResult {
  
  private boolean rightTagName = false;
  private boolean allAttributesFound = false;
  private List<String> attrsToFind;
  
  public ElementResultByName(Task task, String tagName, String elementName, String attributes) {
    super(task, tagName, elementName);
    attrsToFind = Arrays.asList(attributes.split(";"));
  }
  
  private boolean checkAttributes(WebElement element) {
    boolean attributesFound = true;
    for(String att: attrsToFind) {
      String key = att.split("=")[0], value = att.split("=")[1];
      AttributeResult result = new AttributeResult(element, key, value);
      if(!result.isFound())
        attributesFound = false;
      attrs.add(new AttributeResult(element, key, value));
    }
    return attributesFound;
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(elementName));
    if(foundElements.isEmpty()) {
      setResult(Success.NONE, "Es wurde kein Element mit dem Namen \"" + elementName + "\" gefunden");
      return;
    }
    
    foundElements = filterForTagName(foundElements, tag);
    if(foundElements.isEmpty()) {
      setResult(Success.NONE, "Keines der gefundenen Elemente hat den passenden Tag \"" + tag + "\"!");
      return;
    }
    
    rightTagName = true;
    
    if(foundElements.size() > 1)
      message = "Es wurde mehr als 1 Element mit passendem Namen und passendem Tag gefunden. Verwende das erste für weitere Korrektur. ";
    WebElement element = foundElements.get(0);
    
    allAttributesFound = checkAttributes(element);
    
    if(allAttributesFound)
      setResult(Success.COMPLETE, "Alle Attribute wurden gefunden.");
    else
      setResult(Success.PARTIALLY, "Mindestens 1 Attribut wurde nicht gefunden!");
    
  }
  
  private List<WebElement> filterForTagName(List<WebElement> foundElements, String tagName) {
    return foundElements.parallelStream().filter(element -> element.getTagName().equals(tagName))
        .collect(Collectors.toList());
  }
  
  public boolean rightTagNameWasUsed() {
    return rightTagName;
  }
}
