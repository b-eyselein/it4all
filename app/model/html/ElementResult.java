package model.html;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;

public abstract class ElementResult {
  
  protected WebDriver dri;
  
  protected String tag;
  protected String elementName;
  
  protected int ex, subEx, ts;
  
  protected boolean elementFound;
  
  protected List<AttributeResult> attrs = new LinkedList<AttributeResult>();
  
  public ElementResult(WebDriver driver, String tagName, String elementName, int exercise, int subExercise, int task,
      String... attributes) {
    dri = driver;
    
    tag = tagName;
    this.elementName = elementName;
    
    ex = exercise;
    subEx = subExercise;
    ts = task;
    
    evaluate(attributes);
  }
  
  protected abstract void evaluate(String attrsToFind[]);
  
  public String getElementName() {
    return tag;
  }
  
  public void setElementFound(boolean found) {
    elementFound = true;
  }
  
  public boolean isFound() {
    return elementFound;
  }
  
  @Override
  public String toString() {
    String ret = (elementFound ? "+ " : "- ") + "(" + ex + "_" + subEx + "_" + ts + ") " + elementName + " {";
    List<String> attrResults = attrs.stream()
        .map(att -> (att.isFound() ? "+" : "-") + att.getAttributeName() + "=" + att.getAttributeValue())
        .collect(Collectors.toList());
    
    ret += String.join(";", attrResults) + "}";
    return ret;
  }
}
