package model.html.result;

import model.html.task.ChildTask;
import model.html.task.Task;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

public class ChildResult {
  
  private ChildTask task;
  private Success success = Success.NONE;
  private String key = "";
  private String value = "";

  public ChildResult(ChildTask theTask) {
    task = theTask;

    String[] keyAndValue = task.attributes.split(Task.KEY_VALUE_CHARACTER);
    if(keyAndValue.length > 0)
      key = keyAndValue[0];
    if(keyAndValue.length == 2)
      value = keyAndValue[1];
  }

  public void evaluate(SearchContext element) {
    // TODO: Do not use @... in XPath, since it is not working...
    String xpathQuery = "./" + task.tagName;
    List<WebElement> foundElements = element.findElements(By.xpath(xpathQuery));
    for(WebElement child: foundElements) {
      if(child.getAttribute(key).equals(value)) {
        success = Success.COMPLETE;
        return;
      }
    }
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
