package model.html;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import model.Success;
import model.Task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MultiElementResultByName extends ElementResult {
  
  protected List<String> commonAttrs;
  
  private List<String> differentAttrs;
  private HashMap<String, WebElement> singleResults;
  
  public MultiElementResultByName(Task task, String tagName, String elementName, List<String> commonAttributes,
      List<String> differentAttributes) {
    super(task, tagName, elementName);
    commonAttrs = commonAttributes;
    differentAttrs = differentAttributes;
    
    singleResults = new HashMap<String, WebElement>();
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    // elementName muss bei allen Element gleich sein
    List<WebElement> foundElements = driver.findElements(By.name(elementName));
    
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
    
    int i = 0;
    for(WebElement element: foundElements)
      singleResults.put(differentAttrs.get(i++), element);
    
    if(singleResults.size() == differentAttrs.size())
      setSuccess(Success.COMPLETE);
    else if(singleResults.size() > 0)
      setSuccess(Success.PARTIALLY);
    else
      setSuccess(Success.NONE);
    
  }
  
}
