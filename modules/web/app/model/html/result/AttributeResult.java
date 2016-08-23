package model.html.result;

import org.openqa.selenium.WebElement;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import play.Logger;

public class AttributeResult extends EvaluationResult {

  private String key;
  private String value;

  public AttributeResult(String attributeKey, String attributeValue) {
    super(Success.NONE);
    key = attributeKey;
    value = attributeValue;
  }

  public void evaluate(WebElement element) {
    String foundValue = null;
    try {
      foundValue = element.getAttribute(key);
    } catch (NoSuchMethodError e) {
      // TODO: Better solution! Catch NoSuchMethodError if attribute is not
      // declared
      Logger.error("Error while searching for attribute: ", e);
    }
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
