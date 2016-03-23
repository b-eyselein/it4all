package model.html.result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.html.task.NameTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NameResult extends ElementResultWithAttributes<NameTask> {
  
  private boolean rightTagName = false;
  
  public NameResult(NameTask task) {
    super(task);
  }
  
  @Override
  public Success evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(task.elemName));
    if(foundElements.isEmpty())
      return Success.NONE;
    
    foundElements = filterForTagName(foundElements, task.tagName);
    if(foundElements.isEmpty())
      return Success.NONE;
    
    rightTagName = true;
    
    // FIXME!
    // if(foundElements.size() > 1)
    
    WebElement element = foundElements.get(0);
    
    if(checkAttributes(element))
      return Success.COMPLETE;
    else
      return Success.PARTIALLY;
    
  }
  
  public boolean rightTagNameWasUsed() {
    return rightTagName;
  }
  
  @Override
  protected List<String> getAttributesAsJson() {
    if(attributesToFind.isEmpty())
      return Collections.emptyList();
    else
      return attributesToFind.parallelStream().map(attrRes -> attrRes.toJSON()).collect(Collectors.toList());
  }
  
  @Override
  protected List<String> getMessagesAsJson() {
    if(success == Success.NONE)
      return Arrays.asList("{\"suc\": \"-\", \"mes\": \"Element wurde nicht gefunden!\"}");
    else
      return Arrays.asList("{\"suc\": \"+\", \"mes\": \"Element wurde gefunden.\"}");
  }
}
