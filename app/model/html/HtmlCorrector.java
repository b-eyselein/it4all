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
    String newUrl = "http://localhost:9000" + solutionUrl;
    driver.get(newUrl);
    
    result.add(new ElementResult(driver, "input", "name", 1, 1, 1, "type=text", "required=true"));
    
    result.add(new ElementResult(driver, "input", "email", 1, 1, 2, "type=email"));
    
    result.add(new ElementResult(driver, "input", "passwort", 1, 1, 3, "type=password", "required=true"));
    
    for(ElementResult res: result)
      System.out.println(res);
    System.out.println("------------------------------------------------------------------");
    
    return result;
  }
  
}
