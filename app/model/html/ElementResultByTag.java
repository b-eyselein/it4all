package model.html;

import java.util.List;
import java.util.stream.Collectors;

import model.Task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResultByTag extends ElementResult {
  
  public ElementResultByTag(WebDriver driver, Task task, String tagName, String elementName, String... attributes) {
    super(driver, task, tagName, elementName, attributes);
  }
  
  @Override
  protected void evaluate(String attrsToFind[]) {
    List<WebElement> foundElements = dri.findElements(By.tagName(tag));
    if(!elementName.isEmpty())
      foundElements = foundElements.stream().filter(element -> element.getAttribute("type").equals(elementName))
          .collect(Collectors.toList());
    
    // FIXME: multiple elements possible! (--> <input type="reset"><input
    // type="submit">
    for(WebElement element: foundElements) {
      
      for(String att: attrsToFind) {
        String key = att.split("=")[0], value = att.split("=")[1];
        
        String foundAttribute = element.getAttribute(key);
        if(foundAttribute != null && foundAttribute.equals(value)) {
          setElementFound(true);
          attrs.add(new AttributeResult(element, key, value));
          break;
        }
      }
    }
  }
}
