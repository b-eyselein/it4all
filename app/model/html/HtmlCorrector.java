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
    //FIXME: get real port!
    String newUrl = "http://localhost:9000" + solutionUrl;
    driver.get(newUrl);
    
    result.add(new ElementResult(driver, "input", "name", "type=text", "required=true"));
    
    result.add(new ElementResult(driver, "input", "email", "type=email", "required=true"));
    
    result.add(new ElementResult(driver, "input", "passwort", "type=password"));
    
    for(ElementResult theResult: result)
      System.out.println(theResult.toString());

    System.out.println();
    return result;
  }
  
}
