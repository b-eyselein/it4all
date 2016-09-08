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
    super(Success.NONE);
    childTask = theChildTask;

    String[] keyAndValue = childTask.definingAttribute.split(HtmlTask.KEY_VALUE_CHARACTER);
    if(keyAndValue.length > 0)
      definingAttributeKey = keyAndValue[0];
    if(keyAndValue.length == 2)
      definingAttributeValue = keyAndValue[1];
  }

  public void evaluate(SearchContext element) {
    // HINT: Do not use [@...] in XPath-Query, since it is not working with e.
    // g. automatic value attribute in <option>Value</option>
    String xpathQuery = "./" + childTask.tagName;

    List<WebElement> foundElements = element.findElements(By.xpath(xpathQuery));
    for(WebElement child: foundElements) {
      if(child.getAttribute(definingAttributeKey).equals(definingAttributeValue)) {
        success = Success.COMPLETE;
        return;
      }
    }
  }

  @Override
  public String getAsHtml() {
    // FIXME Auto-generated method stub
    return null;
  }

  public String getKey() {
    return definingAttributeKey;
  }

  public String getValue() {
    return definingAttributeValue;
  }

}
