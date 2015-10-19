package model.html;

import java.util.LinkedList;
import java.util.List;

import model.Exercise;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector {
  
  public static List<ElementResult> correct(String solutionUrl, Exercise exercise) {
    LinkedList<ElementResult> result = new LinkedList<ElementResult>();
    
    WebDriver driver = new HtmlUnitDriver();
    // FIXME: get real port!
    String newUrl = "https://www.it4all.uni-wuerzburg.de" + solutionUrl;
    driver.get(newUrl);
    
    result.add(new ElementResultByTag(driver, "form", "", 1, 1, 1, "method=post", "action=test"));
    
    result.add(new ElementResultByName(driver, "input", "name", 1, 1, 2, "type=text", "required=true"));
    
    result.add(new ElementResultByName(driver, "input", "email", 1, 1, 3, "type=email", "required=true"));
    
    result.add(new ElementResultByName(driver, "input", "passwort", 1, 1, 4, "type=password", "required=true"));
    
    result.add(new ElementResultByName(driver, "input", "agb", 1, 1, 5, "type=checkbox", "required=true"));
    
    result.add(new ElementResultByName(driver, "input", "reset", 1, 1, 6, "type=reset"));
    
    result.add(new ElementResultByName(driver, "input", "submit", 1, 1, 7, "type=submit"));
    
    return result;
  }
  
}
