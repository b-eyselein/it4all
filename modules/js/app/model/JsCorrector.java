package model;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import model.JsExercise.JsDataType;
import model.exercise.Success;
import model.programming.ProgLangCorrector;
import model.result.EvaluationResult;
import play.Logger;

public class JsCorrector extends ProgLangCorrector<JsTestData, JsExercise> {
  
  public JsCorrector() {
    super("nashorn");
  }
  
  public static <T> boolean validateResult(T gottenResult, T awaitedResult) {
    return gottenResult.equals(awaitedResult);
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
        String toEvaluate = data.buildToEvaluate(exercise.functionname);
        Object gottenResult = engine.eval(toEvaluate);
        
        boolean validated = validateResult(exercise, data, toEvaluate, gottenResult, data.getOutput())
            .getSuccess() == Success.COMPLETE;
        data.setOk(validated);
      } catch (ScriptException e) {
        Logger.error("Error while validating test data: ", e);
      }
    });
  }
  
  @Override
  protected EvaluationResult validateResult(JsExercise exercise, JsTestData testData, String toEvaluate,
      Object realResult, Object awaitedResult) {
    JsDataType type = exercise.returntype;
    boolean validated = false;
    
    switch(type) {
    // FIXME: implement!!!!!
    case NUMBER:
      if(realResult != null && awaitedResult != null && !realResult.toString().isEmpty()
          && !awaitedResult.toString().isEmpty())
        validated = validateResult(Double.parseDouble(realResult.toString()),
            Double.parseDouble(awaitedResult.toString()));
      break;
    case STRING:
      validated = validateResult(realResult.toString(), awaitedResult);
      break;
    case BOOLEAN:
    case NULL:
    case OBJECT:
    case SYMBOL:
    case UNDEFINED:
    default:
      validated = false;
    }
    
    return new JsTestResult(testData.getOutput(), validated ? Success.COMPLETE : Success.PARTIALLY, toEvaluate,
        realResult != null ? realResult.toString() : "null");
    
  }
  
}
