package model.javascript;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import play.Logger;

public class JsCorrector {
  
  public static List<JsTestResult> correct(JsExercise exercise, String learnerSolution) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    try {
      // Lese programmierte Lernerlösung ein
      engine.eval(learnerSolution);
    } catch (ScriptException e) {
      // TODO: Log Exception or submit to learner?
      Logger.error("Fehler beim Laden der Lernerlösung", e);
      return Collections.emptyList();
    }
    
    // Evaluiere Lernerlösung mit Testwerten
    return exercise.functionTests.stream().map(test -> test.evaluate(engine)).collect(Collectors.toList());
    
  }
  
  public static String correctWeb(JsWebExercise exercise, String solutionUrl) {
    WebDriver driver = loadWebSite(solutionUrl);
    
    WebElement button = driver.findElement(By.xpath("//input[@type='button']"));
    WebElement counter = driver.findElement(By.id("counter"));
    
    if(!counter.getText().equals("0"))
      return "FEHLER!";
    
    // Perform 10 clicks
    for(int i = 0; i < 10; i++) {
      if(!counter.getText().equals(i + ""))
        return "FEHLER bei Vorbedingung: TODO!";
      
      button.click();
      
      if(!counter.getText().equals((i + 1) + ""))
        return "FEHLER bei Nachbedingung: TODO!";
    }
    return "correct...";
  }
  
  private static WebDriver loadWebSite(String solutionUrl) {
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    return driver;
  }
}
