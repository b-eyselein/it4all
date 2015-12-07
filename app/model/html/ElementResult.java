package model.html;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.Success;
import model.Task;

import org.openqa.selenium.WebDriver;

public abstract class ElementResult {
  
  protected Task theTask;
  
  protected String tag;
  protected String elementName;
  
  protected Success success = Success.NONE;
  protected String message = "";
  
  protected List<AttributeResult> attrs = new LinkedList<AttributeResult>();
  
  public ElementResult(Task task, String tagName, String elementName) {
    theTask = task;
    tag = tagName;
    this.elementName = elementName;
  }
  
  public boolean allAttributesFound() {
    return attrs.stream().filter(attr -> attr.isFound()).collect(Collectors.counting()) == attrs.size();
  }
  
  public abstract void evaluate(WebDriver driver);
  
  public List<AttributeResult> getAttributes() {
    return attrs;
  }
  
  public String getElementName() {
    return elementName;
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
