package model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import model.JsExercise.JsDataType;
import model.result.CompleteResult;
import model.result.EvaluationFailed;
import model.result.EvaluationResult;
import model.result.JsCorrectionResult;
import play.Logger;

public class JsCorrector {
  
  private JsCorrector() {
    
  }
  
  public static CompleteResult correct(JsExercise exercise, String learnerSolution,
      List<CommitedTestData> userTestData) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    
    // TODO: Musteroutput mit gegebener Musterlösung berechnen statt angeben?
    
    // Evaluate leaner solution
    try {
      engine.eval(learnerSolution);
    } catch (ScriptException e) { // NOSONAR
      return new JsCorrectionResult(learnerSolution,
          Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Einlesen ihrer Lösung:",
              "<pre>" + e.getLocalizedMessage() + "</pre>")));
    }
    
    List<ITestData> testData = new LinkedList<>();
    testData.addAll(exercise.functionTests);
    testData.addAll(userTestData);
    
    List<EvaluationResult> results = testData.stream().map(test -> test.evaluate(engine)).collect(Collectors.toList());
    return new JsCorrectionResult(learnerSolution, results);
    
  }
  
  public static boolean validateResult(JsDataType type, Object gottenResult, String awaitedResult) {
    switch(type) {
    // FIXME: implement!!!!!
    case NUMBER:
      if(gottenResult == null || awaitedResult == null || gottenResult.toString().isEmpty() || awaitedResult.isEmpty())
        return false;
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
  
}
