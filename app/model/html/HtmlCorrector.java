package model.html;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Corrector;
import model.Exercise;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector extends Corrector<Exercise> {
  
  @Override
  public List<String> correct(String solutionUrl, Exercise exercise) {
    LinkedList<String> result = new LinkedList<String>();
    
    // FIXME: correct with Selenium!!!!
    WebDriver driver = new HtmlUnitDriver();
    String newUrl = "http://localhost:9000" + solutionUrl;
    driver.get(newUrl);
    
    HashMap<String, String> nameAttributes = new HashMap<String, String>();
    nameAttributes.put("type", "text");
    nameAttributes.put("required", "true");
    result.add(findElement(driver, "name", "input", nameAttributes));
    
    HashMap<String, String> emailAttributes = new HashMap<String, String>();
    emailAttributes.put("type", "email");
    result.add(findElement(driver, "email", "input", emailAttributes));
    return result;
  }
  
  private String findElement(WebDriver driver, String elementName, String tagName, Map<String, String> attributes) {
    boolean tagFound = false;
    List<WebElement> allTags = driver.findElements(By.name(elementName));
    for(WebElement tag: allTags)
      if(tag.getTagName().equals(tagName)) {
        // Tag found, 1 Point
        boolean allAttributesFound = true;
        for(String att: attributes.keySet()) {
          String foundAttribute = tag.getAttribute(att);
          if(foundAttribute == null || !foundAttribute.equals(attributes.get(att)))
            // Attribute found, 0.5 Points
            allAttributesFound = false;
        }
        tagFound = allAttributesFound;
      }
    if(tagFound)
      return "+ " + elementName + "-input gefunden!";
    else
      return "- " + elementName + "-input nicht gefunden!";
  }
  
}
