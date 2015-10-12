package model.html;

import java.util.LinkedList;
import java.util.List;

import model.Corrector;
import model.Exercise;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector extends Corrector<Exercise> {
  
  public HtmlCorrector(Exercise exercise) {
    super(exercise);
  }
  
  public HtmlCorrector() {
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
    
    boolean nameFound = false;
    List<WebElement> names = driver.findElements(By.name("name"));
    for(WebElement name: names)
      if(name.getTagName().equals("input") && name.getAttribute("type").equals("text"))
        nameFound = true;
    
    if(nameFound)
      result.add("+ Name-Input gefunden.");
    else
      result.add("- Name-Input nicht gefunden!");
    
    boolean emailFound = false;
    List<WebElement> emails = driver.findElements(By.name("email"));
    for(WebElement email: emails)
      if(email.getTagName().equals("input") && email.getAttribute("type").equals("email"))
        emailFound = true;
    
    if(emailFound)
      result.add("+ Email-input gefunden!");
    else
      result.add("- Email-input nicht gefunden!");
    
    return result;
  }
  
}
