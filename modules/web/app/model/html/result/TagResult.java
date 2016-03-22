package model.html.result;

import java.util.List;

import model.html.task.TagTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TagResult extends ElementResult<TagTask> {

  // FIXME: Nicht verwendet, später komplette Umstellung, um Elementname nicht
  // angeben zu müssen?
  public TagResult(TagTask task, String tagName, String attributes) {
    super(task, tagName, attributes);
  }

  @Override
  public void evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.tagName(task.tagName));
    if(foundElements.isEmpty()) {
      setResult(Success.NONE, "Es wurde kein Element mit dem Tag '" + task.tagName + "' gefunden");
      return;
    }

    if(foundElements.size() > 1)
      message = "Es wurde mehr als 1 Element mit passendem Namen und passendem Tag gefunden. Verwende das erste für weitere Korrektur. ";
    WebElement element = foundElements.get(0);

    if(checkAttributes(element))
      setResult(Success.COMPLETE, "Alle Attribute wurden gefunden.");
    else
      setResult(Success.PARTIALLY, "Mindestens 1 Attribut wurde nicht gefunden!");
  }

}
