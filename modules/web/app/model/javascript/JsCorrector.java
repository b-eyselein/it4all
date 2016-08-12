package model.javascript;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openqa.selenium.WebDriver;
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
  
  public static List<JsWebTest> correctWeb(JsWebExercise exercise, String solutionUrl) {
    WebDriver driver = loadWebSite(solutionUrl);
    
    List<JsWebTest> tests = exercise.getTests();
    tests.forEach(test -> test.test(driver));
    
    return tests;
  }
  
  private static WebDriver loadWebSite(String solutionUrl) {
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    return driver;
  }
}
