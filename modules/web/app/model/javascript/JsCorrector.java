package model.javascript;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.result.EvaluationFailed;
import model.result.EvaluationResult;
import play.Logger;

public class JsCorrector {

  private JsCorrector() {

  }

  public static List<EvaluationResult> correctWeb(JsWebExercise exercise, String solutionUrl) {
    try {
      WebDriver driver = new HtmlUnitDriver(true);
      driver.get(solutionUrl);

      return exercise.tests.stream().map(test -> test.test(driver)).collect(Collectors.toList());
    } catch (WebDriverException e) { // NOSONAR
      // SyntaxFehler in Skript
      EvaluationResult result;
      if(e.getCause() == null)
        result = new EvaluationFailed(e.getMessage());
      else
        result = new EvaluationFailed(e.getCause().getMessage());
      return Arrays.asList(result);
    } catch (Exception e) {
      Logger.error("Another error while correctin jsweb: ", e);
      return Arrays.asList(new EvaluationFailed(e.getMessage()));
    }
  }

  public static <T> boolean validateResult(T gottenResult, T awaitedResult) {
    return gottenResult.equals(awaitedResult);
  }

}