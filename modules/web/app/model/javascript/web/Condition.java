package model.javascript.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Condition {
  
  private String xpathQuery;

  private String innerHTML;

  public Condition(String theXpathQuery, String theInnerHTML) {
    xpathQuery = theXpathQuery;
    innerHTML = theInnerHTML;
  }

  public String getDescription() {
    return "Element mit XPath \"" + xpathQuery + "\" sollte inneren Text \"" + innerHTML + "\" haben";
  }

  public boolean test(WebDriver driver) {
    WebElement element = driver.findElement(By.xpath(xpathQuery));
    return element != null && element.getText().equals(innerHTML);
  }

}
