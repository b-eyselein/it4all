package model.html.result;

import org.openqa.selenium.WebElement;

public class AttributeResult {
  
  private String key;
  private String value;
  
  private boolean found;
  
  public AttributeResult(String attributeName, String attributeValue) {
    key = attributeName;
    value = attributeValue;
  }
  
  public boolean evaluate(WebElement element) {
    String foundAttribute = element.getAttribute(key);
    found = foundAttribute != null && foundAttribute.equals(value);
    return found;
  }
  
  public String getAttributeName() {
    return key;
  }
  
  public String getAttributeValue() {
    return value;
  }
  
  public boolean isFound() {
    return found;
  }
  
  public String toJSON() {
    String json = "{";
    
    // Success
    json += "\"suc\":\"" + (found ? "+" : "-") + "\", ";
    
    // Key and Value
    json += "\"key\":\"" + key + "\", ";
    json += "\"value\":\"" + value + "\"";
    
    json += "}";
    return json;
  }
  
}
