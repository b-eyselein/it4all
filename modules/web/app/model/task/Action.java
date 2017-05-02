package model.task;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

@Embeddable
public class Action {

  public enum ActionType {
    CLICK, FILLOUT;
  }

  @Enumerated(EnumType.STRING)
  public ActionType actiontype;

  public String actionXpathQuery; // NOSONAR

  public String keysToSend; // NOSONAR

  public boolean perform(SearchContext context) {
    switch(actiontype) {
    case CLICK:
      return performClickAction(context);
    case FILLOUT:
      return performFilloutAction(context);
    default:
      return false;
    }
  }

  private boolean performClickAction(SearchContext context) {
    WebElement element = context.findElement(By.xpath(actionXpathQuery));
    if(element == null)
      return false;

    element.click();

    return true;
  }

  private boolean performFilloutAction(SearchContext context) {
    WebElement element = context.findElement(By.xpath(actionXpathQuery));
    if(element == null)
      return false;

    element.sendKeys(keysToSend);

    // click on other element to fire the onchange event...
    context.findElement(By.xpath("//body")).click();

    return true;
  }

}
