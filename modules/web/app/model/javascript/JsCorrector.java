package model.javascript;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.javascript.JsExercise.JsDataType;
import model.javascript.web.JsWebExercise;
import model.javascript.web.JsWebTestResult;
import play.Logger;

public class JsCorrector {

  public static List<EvaluationResult> correct(JsExercise exercise, String learnerSolution,
      List<CommitedTestData> userTestData) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    // FIXME: scriptcontext for console.log() ?!?

    // FIXME: Musteroutput mit gegebener Musterlösung berechnen statt angeben?

    // Evaluate leaner solution
    try {
      engine.eval(learnerSolution);
    } catch (ScriptException e) {
      return Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Einlesen ihrer Lösung:",
          "<pre>" + e.getLocalizedMessage() + "</pre>"));
    }

    List<ITestData> testData = new LinkedList<>();
    testData.addAll(exercise.functionTests);
    testData.addAll(userTestData);

    return testData.stream().map(test -> test.evaluate(engine)).collect(Collectors.toList());

  }

  public static List<JsWebTestResult> correctWeb(JsWebExercise exercise, String solutionUrl) {
    WebDriver driver = loadWebSite(solutionUrl);

    List<JsWebTestResult> results = exercise.tests.stream().map(test -> test.test(driver)).collect(Collectors.toList());
    return results;
  }

  public static boolean validateResult(JsDataType type, Object gottenResult, String awaitedResult) {
    switch(type) {
    // FIXME: implement!!!!!
    case NUMBER:
      return validateResult(Double.parseDouble(gottenResult.toString()), Double.parseDouble(awaitedResult));
    case STRING:
      return validateResult(gottenResult.toString(), awaitedResult);
    case BOOLEAN:
    case NULL:
    case OBJECT:
    case SYMBOL:
    case UNDEFINED:
    default:
      return false;
    }

  }

  public static <T> boolean validateResult(T gottenResult, T awaitedResult) {
    return gottenResult.equals(awaitedResult);
  }

  public static void validateTestData(JsExercise exercise, List<CommitedTestData> testData) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    try {
      engine.eval(exercise.sampleSolution);
    } catch (ScriptException e) {
      Logger.error("Error while validating test data: ", e);
      testData.forEach(data -> data.setOk(false));
      return;
    }

    testData.forEach(data -> {
      try {
        String toEvaluate = data.buildToEvaluate();
        Object gottenResult = engine.eval(toEvaluate);

        boolean validated = validateResult(exercise.returntype, gottenResult, data.getOutput());
        data.setOk(validated);
      } catch (ScriptException e) {
        Logger.error("Error while validating test data: ", e);
      }
    });
  }

  private static WebDriver loadWebSite(String solutionUrl) {
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    return driver;
  }
}
