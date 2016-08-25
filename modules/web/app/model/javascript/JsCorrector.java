package model.javascript;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.exercise.Success;
import model.javascript.web.JsWebExercise;
import model.javascript.web.JsWebTestResult;
import play.Logger;

public class JsCorrector {

  public static List<JsTestResult> correct(JsExercise exercise, String learnerSolution) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    try {
      // Lese programmierte Lernerlösung ein
      engine.eval(learnerSolution);
    } catch (ScriptException e) {
      // FIXME javax.script.ScriptException an Lerner! --> Syntaxfehler!
      // FIXME: Testdaten des Nutzers!
      // TODO: Log Exception or submit to learner?
      Logger.error("Fehler beim Laden der Lernerlösung", e);
      return Arrays.asList(new JsTestResult(exercise.functionTests.get(0), Success.NONE, "", ""));
    }

    // FIXME: Es gab einen Fehler beim Ausführen des Codes. Woher??

    // Evaluiere Lernerlösung mit Testwerten
    return exercise.functionTests.stream().map(test -> test.evaluate(engine)).collect(Collectors.toList());

  }

  public static List<JsWebTestResult> correctWeb(JsWebExercise exercise, String solutionUrl) {
    WebDriver driver = loadWebSite(solutionUrl);

    List<JsWebTestResult> results = exercise.tests.stream().map(test -> test.test(driver)).collect(Collectors.toList());

    return results;
  }

  public static void validateTestData(JsExercise exercise, List<TestData> testData) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    try {
      engine.eval(exercise.sampleSolution);
      for(TestData data: testData)
        data.setOk(engine.eval(exercise.buildToEvaluate(data.getInput())).toString().equals(data.getOutput()));

    } catch (ScriptException e) {
      Logger.error("Error while validating test data: ", e);
    }
  }

  private static WebDriver loadWebSite(String solutionUrl) {
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    return driver;
  }
}
