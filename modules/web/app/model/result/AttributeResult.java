package model.result;

import org.openqa.selenium.WebElement;

import model.Attribute;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;
import play.Logger;

public class AttributeResult extends EvaluationResult {

  private Attribute attribute;

  public AttributeResult(Attribute theAttribute) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, Success.NONE);
    attribute = theAttribute;
  }

  public void evaluate(WebElement element) {
    String foundValue = null;
    try {
      foundValue = element.getAttribute(attribute.key);
    } catch (NoSuchMethodError e) { // NOSONAR
      // FIXME: Better solution! NoSuchMethodError if attribute not declared
      Logger.info("Error while searching for attribute in model.html.result.AttributeResult"
          + ", line 28: NoSuchMethodError...");
    }
    if(foundValue == null)
      success = Success.NONE;
    else if(foundValue.contains(attribute.value))
      success = Success.COMPLETE;
    else
      success = Success.PARTIALLY;
  }

  public String getAsHtml() {
    // FIXME: implement feedbackLevel!
    String ret = "<div class=\"alert alert-" + getBSClass() + "\">Attribut \"" + attribute.key + "\"";
    if(success == Success.COMPLETE)
      ret += " hat den gesuchten Wert.";
    else if(success == Success.PARTIALLY)
      ret += " hat nicht den gesuchten Wert \"" + attribute.value + "\"!";
    else if(success == Success.NONE)
      ret += " konnte nicht gefunden werden!";
    ret += DIV_END;
    return ret;
  }

  public String getKey() {
    return attribute.key;
  }

  public String getValue() {
    return attribute.value;
  }

}
