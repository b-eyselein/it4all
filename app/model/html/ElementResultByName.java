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
  private boolean allAttributesFound = true;
  private List<String> attrsToFind;
  
  public ElementResultByName(Task task, String tagName, String elementName, String attributes) {
    super(task, tagName, elementName);
    attrsToFind = Arrays.asList(attributes.split(";"));
  }
  
  public boolean rightTagNameWasUsed() {
    return rightTagName;
  }

  @Override
  public String getElementNotFoundMessage() {
    String ret = "Element " + elementName + " wurde nicht gefunden.";
    if(!rightTagName)
      ret += "Vielleicht wurde der falsche TagName verwendet?";
    return ret;
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(elementName));
    
    foundElements.parallelStream().filter(element -> {
      // TODO: Fehler, dass falsches Tag verwendet wurde!
        if(!element.getTagName().equals(tag))
          return false;
        else
          rightTagName = true;
        for(String att: attrsToFind) {
          String key = att.split("=")[0], value = att.split("=")[1];
          AttributeResult result = new AttributeResult(element, key, value);
          if(!result.isFound())
            allAttributesFound = false;
          attrs.add(new AttributeResult(element, key, value));
        }
        return true;
      }).collect(Collectors.toList());
    
    if(!foundElements.isEmpty() && allAttributesFound)
      setSuccess(Success.COMPLETE);
    else if(!foundElements.isEmpty() && foundElements.size() > 0)
      setSuccess(Success.PARTIALLY);
    else
      setSuccess(Success.NONE);
  }
}
