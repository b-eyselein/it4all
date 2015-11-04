package model.html;

import java.util.List;
import java.util.stream.Collectors;

import model.Task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResultByTag extends ElementResult {
  
  private String attrsToFind;
  
  public ElementResultByTag(Task task, String tagName, String elementName, String attributes) {
    super(task, tagName, elementName);
    attrsToFind = attributes;
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    //FIXME: Nicht verwendet, später komplette Umstellung, um Elementname nicht angeben zu müssen?
    List<WebElement> foundElements = driver.findElements(By.tagName(tag));
    if(!elementName.isEmpty())
      foundElements = foundElements.stream().filter(element -> element.getAttribute("type").equals(elementName))
          .collect(Collectors.toList());
    
    // TODO: multiple elements possible! (--> <input type="reset"><input
    // type="submit">
    for(WebElement element: foundElements) {
      for(String att: attrsToFind.split(";")) {
        String key = att.split("=")[0], value = att.split("=")[1];
        
        String foundAttribute = element.getAttribute(key);
        if(foundAttribute != null && foundAttribute.equals(value)) {
          // TODO: setSuccess!
          // setElementFound(true);
          attrs.add(new AttributeResult(element, key, value));
        }
      }
    }
  }
  
}
