package model.html.result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.html.task.TagTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TagResult extends ElementResultWithAttributes<TagTask> {
  
  public TagResult(TagTask task) {
    super(task);
  }
  
  @Override
  public Success evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.tagName(task.tagName));
    if(foundElements.isEmpty())
      return Success.NONE;
    
    // TODO: Mehrere Elemente!
    // if(foundElements.size() > 1)
    // message = "Es wurde mehr als 1 Element mit passendem Namen" +
    // " und passendem Tag gefunden. Verwende das erste für weitere Korrektur. ";
    WebElement element = foundElements.get(0);
    
    if(checkAttributes(element))
      return Success.COMPLETE;
    else
      return Success.PARTIALLY;
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
