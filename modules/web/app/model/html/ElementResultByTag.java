package model.html;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResultByTag extends ElementResult {
  
  // FIXME: Nicht verwendet, später komplette Umstellung, um Elementname nicht
  // angeben zu müssen?
  public ElementResultByTag(Task task, String tagName, String attributes) {
    super(task, tagName, attributes);
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.tagName(tag));
    System.out.println("Looking for Element with tag: " + tag);
    if(foundElements.isEmpty()) {
      System.out.println("\tNo Element found with this tag!");
      setResult(Success.NONE, "Es wurde kein Element mit dem Tag '" + tag + "' gefunden");
      return;
    }
    System.out.println("\t" + foundElements.size() + "Elemente gefunden!");
    
    if(foundElements.size() > 1)
      message = "Es wurde mehr als 1 Element mit passendem Namen und passendem Tag gefunden. Verwende das erste für weitere Korrektur. ";
    WebElement element = foundElements.get(0);
    
    if(checkAttributes(element))
      setResult(Success.COMPLETE, "Alle Attribute wurden gefunden.");
    else
      setResult(Success.PARTIALLY, "Mindestens 1 Attribut wurde nicht gefunden!");
  }
  
}
