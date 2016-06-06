package model.html.result;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.task.ChildTask;
import model.html.task.HtmlTask;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

public class ChildResult extends EvaluationResult {
  
  private ChildTask childTask;
  private String definingAttributeKey = "";
  private String definingAttributeValue = "";

  public ChildResult(ChildTask theChildTask) {
    childTask = theChildTask;

    String[] keyAndValue = childTask.definingAttribute.split(HtmlTask.KEY_VALUE_CHARACTER);
    if(keyAndValue.length > 0)
      definingAttributeKey = keyAndValue[0];
    if(keyAndValue.length == 2)
      definingAttributeValue = keyAndValue[1];
  }

  public void evaluate(SearchContext element) {
    // TODO: Do not use @... in XPath, since it is not working...
    String xpathQuery = "./" + childTask.tagName;
    List<WebElement> foundElements = element.findElements(By.xpath(xpathQuery));
    for(WebElement child: foundElements) {
      if(child.getAttribute(definingAttributeKey).equals(definingAttributeValue)) {
        success = Success.COMPLETE;
        return;
      }
    }
  }

  public String getKey() {
    return definingAttributeKey;
  }

  public String getValue() {
    return definingAttributeValue;
  }

}
