package model.html;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResult {
  
  private WebDriver dri;
  
  private String tag;
  private String element;
  
  private int ex, subEx, ts;
  
  private boolean elementFound;
  
  private List<AttributeResult> attrs = new LinkedList<AttributeResult>();
  
  public ElementResult(WebDriver driver, String tagName, String elementName, int exercise, int subExercise, int task,
      String... attributes) {
    dri = driver;
    
    tag = tagName;
    element = elementName;
    
    ex = exercise;
    subEx = subExercise;
    ts = task;
    
    evaluate(attributes);
  }
  
  private void evaluate(String attrsToFind[]) {
    List<WebElement> foundElements = dri.findElements(By.name(element));
    for(WebElement element: foundElements)
      if(element.getTagName().equals(tag)) {
        setElementFound(true);
        
        for(String att: attrsToFind) {
          String key = att.split("=")[0], value = att.split("=")[1];
          attrs.add(new AttributeResult(element, key, value));
        }
      }
  }
  
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
    String ret = (elementFound ? "+ " : "- ") + "(" + ex + "_" + subEx + "_" + ts + ") " + element + " {";
    List<String> attrResults = attrs.stream()
        .map(att -> (att.isFound() ? "+" : "-") + att.getAttributeName() + "=" + att.getAttributeValue())
        .collect(Collectors.toList());
    
    ret += String.join(";", attrResults) + "}";
    return ret;
  }
}
