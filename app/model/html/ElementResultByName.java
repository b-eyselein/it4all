package model.html;

import java.util.List;

import model.Task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResultByName extends ElementResult {

  private String[] attrsToFind;
  
  public ElementResultByName(WebDriver driver, Task task, String tagName, String elementName, String... attributes) {
    super(driver, task, tagName, elementName);
    attrsToFind = attributes;
  }

  @Override
  public void evaluate() {
    List<WebElement> foundElements = dri.findElements(By.name(elementName));
    for (WebElement element : foundElements)
      if (element.getTagName().equals(tag)) {
        setElementFound(true);

        for(String att: attrsToFind) {
          String key = att.split("=")[0], value = att.split("=")[1];
          attrs.add(new AttributeResult(element, key, value));
        }
      }
  }
}
