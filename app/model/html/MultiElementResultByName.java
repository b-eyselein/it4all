package model.html;

import java.util.List;
import java.util.stream.Collectors;

import model.Task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MultiElementResultByName extends ElementResult {
  
  protected List<String> commonAttrs;
  private List<String> differentAttrs;
  
  public MultiElementResultByName(WebDriver driver, Task task, String tagName, String elementName,
      List<String> commonAttributes, List<String> differentAttributes) {
    super(driver, task, tagName, elementName);
    commonAttrs = commonAttributes;
    differentAttrs = differentAttributes;
  }
  
  @Override
  public void evaluate() {
    // elementName muss bei allen Element gleich sein
    List<WebElement> foundElements = dri.findElements(By.name(elementName));
    
    // Stelle sicher, dass alle gefundenen Elemente alle Common-Attribute
    // besitzen
    foundElements.stream().filter(ele -> {
      for(String att: commonAttrs) {
        String key = att.split("=")[0], value = att.split("=")[1];
        AttributeResult attributeResult = new AttributeResult(ele, key, value);
        if(!attributeResult.isFound())
          return false;
        attrs.add(new AttributeResult(ele, key, value));
      }
      return true;
    }).collect(Collectors.toList());
    
    // Stelle sicher, dass alle Elemente jeweils ein Different-Attribute
    // enthalten
    foundElements.stream().filter(ele -> {
      for(String att: differentAttrs) {
        String key = att.split("=")[0], value = att.split("=")[1];
        AttributeResult attributeResult = new AttributeResult(ele, key, value);
        if(!attributeResult.isFound())
          return true;
        attrs.add(new AttributeResult(ele, key, value));
      }
      return false;
    }).collect(Collectors.toList());
  }
}
