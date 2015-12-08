package model.html;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.Success;
import model.Task;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class ElementResult {
  
  protected Task theTask;
  
  protected String tag;
  
  protected Success success = Success.NONE;
  protected String message = "";
  
  protected List<String> attributesToFind;
  protected List<AttributeResult> attrs = new LinkedList<AttributeResult>();
  
  public ElementResult(Task task, String tagName, String attributes) {
    theTask = task;
    tag = tagName;
    attributesToFind = Arrays.asList(attributes.split(";"));
  }
  
  public boolean allAttributesFound() {
    return attrs.stream().filter(attr -> attr.isFound()).collect(Collectors.counting()) == attrs.size();
  }
  
  protected boolean checkAttributes(WebElement element) {
    boolean attributesFound = true;
    for(String att: attributesToFind) {
      if(!att.isEmpty()) {
        String key = att.split("=")[0], value = att.split("=")[1];
        AttributeResult result = new AttributeResult(element, key, value);
        if(!result.isFound())
          attributesFound = false;
        attrs.add(new AttributeResult(element, key, value));
      }
    }
    return attributesFound;
  }
  
  public abstract void evaluate(WebDriver driver);
  
  public List<AttributeResult> getAttributes() {
    return attrs;
  }
  
  public String getMessage() {
    return message;
  }
  
  public int getPoints() {
    if(success == Success.NONE)
      return 0;
    else if(success == Success.COMPLETE)
      return 2;
    else
      return 1;
  }
  
  public Success getSuccess() {
    return success;
  }
  
  public Task getTask() {
    return theTask;
  }
  
  public void setResult(Success suc, String mes) {
    success = suc;
    message += mes;
  }
  
}
