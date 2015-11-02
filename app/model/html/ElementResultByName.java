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
  
  private List<String> attrsToFind;
  private boolean allAttributesFound = true;
  
  public ElementResultByName(Task task, String tagName, String elementName, String attributes) {
    super(task, tagName, elementName);
    attrsToFind = Arrays.asList(attributes.split(";"));
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(elementName));
    
    foundElements.parallelStream().filter(element -> {
      if(!element.getTagName().equals(tag))
        return false;
      for(String att: attrsToFind) {
        String key = att.split("=")[0], value = att.split("=")[1];
        AttributeResult result = new AttributeResult(element, key, value);
        if(!result.isFound())
          allAttributesFound = false;
        attrs.add(new AttributeResult(element, key, value));
      }
      return true;
    }).peek(System.out::println).collect(Collectors.toList());
    
    System.out.println();
    
    if(allAttributesFound)
      setSuccess(Success.COMPLETE);
    else if(foundElements.size() > 0)
      setSuccess(Success.PARTIALLY);
    else
      setSuccess(Success.NONE);
  }
}
