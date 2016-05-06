package model.html.result;

import org.openqa.selenium.WebElement;

import model.EvaluationResult;
import model.Success;

public class AttributeResult extends EvaluationResult {
  
  private String key;
  private String value;

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

  public String getValue() {
    return value;
  }

}
