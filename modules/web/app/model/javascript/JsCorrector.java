package model.javascript;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.result.EvaluationResult;

public class JsCorrector {
  
  private JsCorrector() {
    
  }
  
  public static List<EvaluationResult> correctWeb(JsWebExercise exercise, String solutionUrl) {
    WebDriver driver = loadWebSite(solutionUrl);
    return exercise.tests.stream().map(test -> test.test(driver)).collect(Collectors.toList());
  }
  
  public static <T> boolean validateResult(T gottenResult, T awaitedResult) {
    return gottenResult.equals(awaitedResult);
  }
  
  private static WebDriver loadWebSite(String solutionUrl) {
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    return driver;
  }
}