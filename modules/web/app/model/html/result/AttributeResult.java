package model.html.result;

import org.openqa.selenium.WebElement;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;
import play.Logger;

public class AttributeResult extends EvaluationResult {
  
  private String key;
  private String value;
  
  public AttributeResult(String attributeKey, String attributeValue) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, Success.NONE);
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
    else if(foundValue.contains(value))
      success = Success.COMPLETE;
    else
      success = Success.PARTIALLY;
  }
  
  @Override
  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    String ret = "<div class=\"alert alert-" + getBSClass() + "\">Attribut \"" + key + "\"";
    if(success == Success.COMPLETE)
      ret += " hat den gesuchten Wert.";
    else if(success == Success.PARTIALLY)
      ret += " hat nicht den gesuchten Wert \"" + value + "\"!";
    else if(success == Success.NONE)
      ret += " konnte nicht gefunden werden!";
    ret += DIV_END;
    return ret;
  }
  
  public String getKey() {
    return key;
  }
  
  public String getValue() {
    return value;
  }
  
}
