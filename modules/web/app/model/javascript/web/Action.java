package model.javascript.web;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
  
  private boolean performClickAction(WebDriver driver) {
    WebElement element = driver.findElement(By.xpath(xpathQuery));
    
    if(element == null)
      return false;
    
    element.click();
    
    return true;
  }
  
  private boolean performFilloutAction(WebDriver driver) {
    WebElement element = driver.findElement(By.xpath(xpathQuery));
    if(element == null)
      return false;
    
    element.sendKeys(keysToSend);
    
    // click on other element to fire the onchange event...
    driver.findElement(By.xpath("//body")).click();
    
    return true;
  }
  
}
