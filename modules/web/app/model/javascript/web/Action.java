package model.javascript.web;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import play.Logger;

@Embeddable
public class Action {

  public static enum ActionType {
    CLICK, FILLOUT;
  }

  @Enumerated(EnumType.STRING)
  public ActionType actiontype;

  public String xpathQuery;
  public String keysToSend;

  public String getDescription() {
    return "TODO!";
  }

  public boolean perform(WebDriver driver) {
    switch(actiontype) {
    case CLICK:
      return performClickAction(driver);
    case FILLOUT:
      return performFilloutAction(driver);
    default:
      return false;
    }
  }

  private void checkAlert(WebDriver driver) {
    try {
      WebDriverWait wait = new WebDriverWait(driver, 2);
      wait.until(ExpectedConditions.alertIsPresent());
      Alert alert = driver.switchTo().alert();
      Logger.info("There was an alert: \"" + alert.getText() + "\"");
      alert.accept();
    } catch (Exception e) {
      // exception handling
    }
  }
  
  private boolean performClickAction(WebDriver driver) {
    WebElement element = driver.findElement(By.xpath(xpathQuery));

    if(element == null)
      return false;

    element.click();
    
    checkAlert(driver);
    
    return true;
  }

  private boolean performFilloutAction(WebDriver driver) {
    WebElement element = driver.findElement(By.xpath(xpathQuery));
    if(element == null)
      return false;

    element.sendKeys(keysToSend);

    // click on other element to fire the onchange event...
    driver.findElement(By.xpath("//body")).click();
    
    checkAlert(driver);

    return true;
  }

}
