package model.js;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import model.CommitedTestData;
import model.ExecutionResult;
import model.ITestData;
import model.ProgrammingExercise;
import model.exercise.Success;
import model.result.EvaluationResult;
import play.Logger;

public class JsCorrector extends model.ProgLangCorrector {

  public JsCorrector() {
    super("nashorn");
  }

  @Override
  protected EvaluationResult validateResult(ProgrammingExercise exercise, ITestData testData, String toEvaluate,
      Object realResult, Object awaitedResult, String output) {
    // JsDataType type = exercise.returntype;
    boolean validated = false;
    
    // switch(type) {
    // // FIXME: implement!!!!!
    // case NUMBER:
    // if(realResult != null && awaitedResult != null &&
    // !realResult.toString().isEmpty()
    // && !awaitedResult.toString().isEmpty())
    // validated = validateResult(Double.parseDouble(realResult.toString()),
    // Double.parseDouble(awaitedResult.toString()));
    // break;
    // case STRING:
    // validated = validateResult(realResult.toString(), awaitedResult);
    // break;
    // case BOOLEAN:
    // case NULL:
    // case OBJECT:
    // case SYMBOL:
    // case UNDEFINED:
    // default:
    // validated = false;
    // }

    return new ExecutionResult(testData.getOutput(), validated ? Success.COMPLETE : Success.PARTIALLY, toEvaluate,
        realResult != null ? realResult.toString() : "null", output);

  }

  public void validateTestData(ProgrammingExercise exercise, List<CommitedTestData> testData) {
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

        boolean validated = validateResult(exercise, data, toEvaluate, gottenResult, data.getOutput(),
            "TODO: output...").getSuccess() == Success.COMPLETE;
        data.setOk(validated);
      } catch (ScriptException e) {
        Logger.error("Error while validating test data: ", e);
      }
    });
  }

}