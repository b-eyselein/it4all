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

public class HtmlCorrector_New extends Corrector<Exercise> {
  
  public HtmlCorrector_New(Exercise exercise) {
    super(exercise);
  }
  
  public HtmlCorrector_New() {
    // TODO: Fix, falls nur eine Aufgabe!!!!!
    super(Exercise.finder.byId(1));
  }
  
  @Override
  public List<String> correct(String url) {
    LinkedList<String> result = new LinkedList<String>();
    
    // FIXME: correct with Selenium!!!!
    WebDriver driver = new HtmlUnitDriver();
    String newUrl = "http://localhost:9000" + url;
    driver.get(newUrl);
    
    HashMap<String, String> nameAttributes = new HashMap<String, String>();
    nameAttributes.put("type", "text");
    nameAttributes.put("required", "required");
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
        boolean allAttributesFound = true;
        for(String att: attributes.keySet()) {
          String foundAttribute = tag.getAttribute(att);
          if(foundAttribute == null || !foundAttribute.equals(attributes.get(att)))
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
