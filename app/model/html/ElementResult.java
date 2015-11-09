package model.html;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.Success;
import model.Task;

import org.openqa.selenium.WebDriver;

public abstract class ElementResult {
  
  protected Task task;
  
  protected String tag;
  protected String elementName;
  
  protected Success success;
  
  protected List<AttributeResult> attrs = new LinkedList<AttributeResult>();
  
  public ElementResult(Task task, String tagName, String elementName) {
    tag = tagName;
    this.elementName = elementName;
    success = Success.NONE;
    this.task = task;
  }
  
  public abstract void evaluate(WebDriver driver);
  
  public String getElementNotFoundMessage() {
    return "Element " + elementName + " wurde nicht gefunden.";
  }
  
  public int getPoints() {
    if(success == Success.NONE)
      return 0;
    else if(success == Success.COMPLETE)
      return 2;
    else
      return 1;
  }
  
  public String getElementName() {
    return elementName;
  }
  
  public boolean allAttributesFound() {
    return attrs.stream().filter(attr -> attr.isFound()).collect(Collectors.counting()) == attrs.size();
  }
  
  public void setSuccess(Success suc) {
    success = suc;
  }
  
  public Task getTask() {
    return task;
  }
  
  public Success getSuccess() {
    return success;
  }
  
  public List<AttributeResult> getAttributes() {
    return attrs;
  }
  
}
