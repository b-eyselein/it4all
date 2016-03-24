package model.html.result;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ChildResult {

  private ChildTask task;
  private Success success = Success.NONE;

  public ChildResult(ChildTask theTask) {
    task = theTask;

  }

  public Success evaluate(WebElement element) {
    String[] keyAndValue = task.attributes.split(ElementResultWithAttributes.KEY_VALUE_CHARACTER);
    String value = "";
    if(keyAndValue.length == 2)
      value = keyAndValue[1];
    for(WebElement child: element.findElements(By.xpath(".//*")))
      if(child.getAttribute(keyAndValue[0]).equals(value))
        return Success.COMPLETE;
    return Success.NONE;
  }

  public Success getSuccess() {
    return success;
  }

  public void setSuccess(Success suc) {
    success = suc;
  }

  public String toJson() {
    // TODO Auto-generated method stub
    return "{\"suc\": \"" + success.getJsonRepresentant() + "\", \"mes\": \"TODO!\"}";
  }
}
