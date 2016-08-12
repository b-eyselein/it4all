package model.javascript.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class Action {
  
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
  
  public abstract String getDescription();
  
  public abstract boolean perform(WebDriver driver);
  
}
