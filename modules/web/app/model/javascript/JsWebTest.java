package model.javascript;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JsWebTest {

  public static class ClickAction {
    private String xpathQuery;

    public ClickAction(String theXpathQuery) {
      xpathQuery = theXpathQuery;
    }

    public void perform(WebDriver driver) {
      driver.findElement(By.xpath(xpathQuery)).click();
    }

  }

  public static class Condition {

    private String xpathQuery;

    private String innerHTML;

    public Condition(String theXpathQuery, String theInnerHTML) {
      xpathQuery = theXpathQuery;
      innerHTML = theInnerHTML;
    }

    public boolean test(WebDriver driver) {
      WebElement element = driver.findElement(By.xpath(xpathQuery));
      return element != null && element.getText().equals(innerHTML);
    }

  }

  private Condition precondition;

  private ClickAction action;

  private Condition postcondition;

  public JsWebTest(String conditionElement, String preCondition, String postCondition, String actionElement) {
    precondition = new Condition(conditionElement, preCondition);
    postcondition = new Condition(conditionElement, postCondition);
    action = new ClickAction(actionElement);
  }

  public boolean test(WebDriver driver) {
    if(!precondition.test(driver))
      return false;

    action.perform(driver);

    if(!postcondition.test(driver))
      return false;

    return true;
  }

}
