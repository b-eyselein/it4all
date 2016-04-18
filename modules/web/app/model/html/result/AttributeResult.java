package model.html.result;

import org.openqa.selenium.WebElement;

public class AttributeResult {
  
  private String key;
  private String value;
  
  private Success success;
  
  public AttributeResult(String attributeKey, String attributeValue) {
    key = attributeKey;
    value = attributeValue;
  }
  
  public void evaluate(WebElement element) {
    String foundValue = element.getAttribute(key);
    if(foundValue == null)
      success = Success.NONE;
    else if(foundValue.equals(value))
      success = Success.COMPLETE;
    else
      success = Success.PARTIALLY;
  }
  
  public String getKey() {
    return key;
  }
  
  public Success getSuccess() {
    return success;
  }
  
  public String getValue() {
    return value;
  }
  
}
