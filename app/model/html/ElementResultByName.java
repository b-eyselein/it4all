package model.html;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResultByName extends ElementResult {
  
  public ElementResultByName(WebDriver driver, String tagName, String elementName, int exercise, int subExercise,
      int task, String... attributes) {
    super(driver, tagName, elementName, exercise, subExercise, task, attributes);
  }
  
  @Override
  protected void evaluate(String attrsToFind[]) {
    List<WebElement> foundElements = dri.findElements(By.name(elementName));
    for(WebElement element: foundElements)
      if(element.getTagName().equals(tag)) {
        setElementFound(true);
        
        for(String att: attrsToFind) {
          String key = att.split("=")[0], value = att.split("=")[1];
          attrs.add(new AttributeResult(element, key, value));
        }
      }
  }
}
