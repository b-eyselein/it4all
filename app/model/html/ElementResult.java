package model.html;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.Task;

import org.openqa.selenium.WebDriver;

public abstract class ElementResult {

  protected WebDriver dri;
  protected Task task;

  protected String tag;
  protected String elementName;

  protected boolean elementFound;

  protected List<AttributeResult> attrs = new LinkedList<AttributeResult>();

  public ElementResult(WebDriver driver, Task task, String tagName, String elementName, String... attributes) {
    dri = driver;

    tag = tagName;
    this.elementName = elementName;

    this.task = task;

    evaluate(attributes);
  }

  protected abstract void evaluate(String attrsToFind[]);

  public double getPoints() {
    double attrsFound = attrs.stream().filter(attr -> attr.isFound()).collect(Collectors.counting()) / 2;
    return isFound() ? 1 + attrsFound : 0;
  }
  
  public String getElementName() {
    return tag;
  }
  
  public boolean allAttributesFound() {
    return attrs.stream().filter(attr -> attr.isFound()).collect(Collectors.counting()) == attrs.size();
  }

  public void setElementFound(boolean found) {
    elementFound = true;
  }

  public Task getTask() {
    return task;
  }
  
  public boolean isFound() {
    return elementFound;
  }
  
  public List<AttributeResult> getAttributes() {
    return attrs;
  }

  @Override
  public String toString() {
    String ret = "(" + task.id + ") " + elementName + " {";
    List<String> attrResults = attrs.stream().map(att -> (att.isFound() ? "+" : "-") + att.getAttributeName() + "=" + att.getAttributeValue())
        .collect(Collectors.toList());

    ret += String.join(";", attrResults) + "}";
    return ret;
  }
}
