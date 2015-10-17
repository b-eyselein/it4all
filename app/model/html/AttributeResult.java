package model.html;

import org.openqa.selenium.WebElement;

public class AttributeResult {
  
  private WebElement element;
  private String key;
  private String value;
  
  private boolean found;
  
  public AttributeResult(WebElement webElement, String attributeName, String attributeValue) {
    element = webElement;
    key = attributeName;
    value = attributeValue;
    evaluate();
  }
  
  private void evaluate() {
    String foundAttribute = element.getAttribute(key);
    if(foundAttribute != null && foundAttribute.equals(value))
      found = true;
  }
  
  public boolean isFound() {
    return found;
  }

  public String getAttributeName() {
    return key;
  }

  public String getAttributeValue() {
    return value;
  }
  
}
