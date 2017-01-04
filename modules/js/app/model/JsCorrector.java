package model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import model.JsExercise.JsDataType;
import model.exercise.Success;
import model.programming.IExecutionResult;
import model.programming.ProgLangCorrector;
import model.result.CompleteResult;
import model.result.EvaluationFailed;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;

public class JsCorrector extends ProgLangCorrector {
  
  public JsCorrector() {
    super("nashorn");
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
  
  public CompleteResult correct(JsExercise exercise, String learnerSolution, List<CommitedTestData> userTestData,
      User.SHOW_HIDE_AGGREGATE todo) {
    ScriptEngine engine = MANAGER.getEngineByName(engineName);
    
    // TODO: Musteroutput mit gegebener Musterlösung berechnen statt angeben?
    
    // Evaluate leaner solution
    try {
      engine.eval(learnerSolution);
    } catch (ScriptException e) { // NOSONAR
      return new CompleteResult(learnerSolution,
          Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Einlesen ihrer Lösung:",
              "<pre>" + e.getLocalizedMessage() + "</pre>")),
          todo);
    }
    
    List<JsTestData> testData = new LinkedList<>();
    testData.addAll(exercise.functionTests);
    testData.addAll(userTestData);
    
    List<EvaluationResult> results = testData.stream().map(test -> evaluate(test, engine)).collect(Collectors.toList());
    
    return new CompleteResult(learnerSolution, results, todo);
    
  }
  
  public void validateTestData(JsExercise exercise, List<CommitedTestData> testData) {
    ScriptEngine engine = MANAGER.getEngineByName(engineName);
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
  
  private JsTestResult evaluate(JsTestData testData, ScriptEngine engine) {
    String toEvaluate = testData.buildToEvaluate();
    IExecutionResult realResult = execute(toEvaluate, engine);
    
    boolean validated = validateResult(testData.getExercise().returntype, realResult.getResult(), testData.getOutput());
    
    return new JsTestResult(testData.getOutput(), validated ? Success.COMPLETE : Success.PARTIALLY, toEvaluate,
        realResult.getResult().toString());
  }
  
}
