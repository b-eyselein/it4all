package model.html;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResult {
  
  private WebDriver dri;
  
  private String tag;
  private String element;
  
  private boolean elementFound;
  
  private List<AttributeResult> attrs = new LinkedList<AttributeResult>();
  
  public ElementResult(WebDriver driver, String tagName, String elementName, String... attributes) {
    dri = driver;
    
    tag = tagName;
    element = elementName;
    
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
    String ret = "";
    ret += elementFound ? "+ " : "- ";
    ret += element + " {\n";
    for(AttributeResult attResult: attrs) {
      ret += "\t" + (attResult.isFound() ? "++ " : "-- ");
      ret += attResult.getAttributeName() + " :: " + attResult.getAttributeValue();
      ret += "\n";
    }
    ret += "}";
    return ret;
  }
  
}
