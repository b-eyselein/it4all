package model.html.result;

import java.util.List;

import model.html.task.NameTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NameResult extends ElementResult<NameTask> {

  private boolean rightTagName = false;

  public NameResult(NameTask task, String tagName, String elementName, String attributes) {
    super(task, tagName, attributes);
  }

  @Override
  public void evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(task.elemName));
    if(foundElements.isEmpty()) {
      setResult(Success.NONE, "Es wurde kein Element mit dem Namen '" + task.elemName + "' gefunden");
      return;
    }

    foundElements = filterForTagName(foundElements, task.tagName);
    if(foundElements.isEmpty()) {
      setResult(Success.NONE, "Keines der gefundenen Elemente hat den passenden Tag '" + task.tagName + "'!");
      return;
    }

    rightTagName = true;

    if(foundElements.size() > 1)
      message = "Es wurde mehr als 1 Element mit passendem Namen und passendem Tag gefunden. Verwende das erste für weitere Korrektur. ";
    WebElement element = foundElements.get(0);

    if(checkAttributes(element))
      setResult(Success.COMPLETE, "Alle Attribute wurden gefunden.");
    else
      setResult(Success.PARTIALLY, "Mindestens 1 Attribut wurde nicht gefunden!");

  }

  public boolean rightTagNameWasUsed() {
    return rightTagName;
  }
}
