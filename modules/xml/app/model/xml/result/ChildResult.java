package model.xml.result;

import model.html.task.ChildTask;
import model.html.task.Task;

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
    for(WebElement child: element.findElements(By.xpath(".//*"))) {
      if(child.getAttribute(key).equals(value)) {
        success = Success.COMPLETE;
        return;
      }
    }
  }

  public Success getSuccess() {
    return success;
  }

  public String toJson() {
    String json = "{\"suc\": \"" + success.getJsonRepresentant() + "\"";
    json += ", ";
    json += "\"mes\": \"" + "Kindelement mit Tag '" + task.tagName + "' und Attribut '" + key + "' mit Wert '" + value
        + "'";
    if(success == Success.COMPLETE)
      json += " wurde erfolgreich gefunden.\"}";
    else if(success == Success.NONE)
      json += " konnte nicht gefunden werden!\"}";
    return json;
  }
}
