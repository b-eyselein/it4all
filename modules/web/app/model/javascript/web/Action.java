package model.javascript.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class Action {

  public static enum ActionType {
    CLICK, FILLOUT;
  }

  public static class ClickAction extends Action {

    private String xpathQuery;

    public ClickAction(String theXpathQuery) {
      xpathQuery = theXpathQuery;
    }

    @Override
    public String getDescription() {
      return "Klicke Element mit XPath \"" + xpathQuery + "\"";
    }

    @Override
    public boolean perform(WebDriver driver) {
      WebElement element = driver.findElement(By.xpath(xpathQuery));

      if(element == null)
        return false;

      element.click();
      return true;
    }

  }

  public static class FillOutAction extends Action {

    private String xpathQuery;
    private String keysToSend;

    public FillOutAction(String theXpathQuery, String theKeysToSend) {
      xpathQuery = theXpathQuery;
      keysToSend = theKeysToSend;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "Schreibe \"" + keysToSend + "\" in Element mit XPath \"" + xpathQuery + "\"";
    }

    @Override
    public boolean perform(WebDriver driver) {
      WebElement element = driver.findElement(By.xpath(xpathQuery));
      if(element == null)
        return false;

      element.sendKeys(keysToSend);

      // TODO: click on other element to fire the onchange event...
      driver.findElement(By.xpath("//body")).click();

      return true;
    }

  }

  public static Action instantiate(ActionType actionType, String xpathQuery, String... otherFeatures) {
    switch(actionType) {
    case CLICK:
      return new ClickAction(xpathQuery);
    case FILLOUT:
      return new FillOutAction(xpathQuery, otherFeatures[0]);
    default:
      return null;
    }
  }

  public abstract String getDescription();

  public abstract boolean perform(WebDriver driver);

}
