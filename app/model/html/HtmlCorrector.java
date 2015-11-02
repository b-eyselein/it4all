package model.html;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import model.Exercise;
import model.Task;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector {
  
  public static List<ElementResult> correct(String solutionUrl, Exercise exercise) {
    LinkedList<ElementResult> result = new LinkedList<ElementResult>();
    
    WebDriver driver = new HtmlUnitDriver();
    
    String newUrl = "http://localhost:9000/" + solutionUrl;
    
    exercise.subExercises.get(0).tasks.get(0);
    
    driver.get(newUrl);
    
    Task t1 = exercise.subExercises.get(0).tasks.get(0);
    Task t2 = exercise.subExercises.get(0).tasks.get(1);
    Task t3 = exercise.subExercises.get(0).tasks.get(2);
    Task t4 = exercise.subExercises.get(0).tasks.get(3);
    Task t5 = exercise.subExercises.get(0).tasks.get(4);
    Task t6 = exercise.subExercises.get(0).tasks.get(5);
    Task t7 = exercise.subExercises.get(0).tasks.get(6);
    Task t8 = exercise.subExercises.get(0).tasks.get(7);
    
    result.add(new ElementResultByTag(t1, "form", "", "method=post;action=test"));
    
    result.add(new ElementResultByName(t2, "input", "name", "type=text;required=true"));
    
    result.add(new ElementResultByName(t3, "input", "email", "type=email;required=true"));
    
    result.add(new ElementResultByName(t4, "input", "passwort", "type=password;required=true"));
    
    result.add(new ElementResultByName(t5, "input", "agb", "type=checkbox;required=true"));
    
    result.add(new MultiElementResultByName(t6, "input", "radio", Collections.emptyList(), Arrays.asList("value=Rad1",
        "value=Rad2", "value=Rad3")));
    
    result.add(new ElementResultByTag(t7, "input", "reset", "type=reset"));
    
    result.add(new ElementResultByName(t8, "input", "submit", "type=submit"));
    
    for(ElementResult res: result)
      evaluateElement(driver, res);
    
    return result;
  }
  
  private static void evaluateElement(WebDriver driver, ElementResult result) {
    result.evaluate(driver);
  }
  
}
