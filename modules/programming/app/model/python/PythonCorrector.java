package model.python;

import model.ExecutionResult;
import model.ITestData;
import model.ProgLangCorrector;
import model.ProgrammingExercise;
import model.exercise.Success;
import model.result.EvaluationResult;

public class PythonCorrector extends ProgLangCorrector {
  
  public PythonCorrector() {
    super("python");
  }
  
  @Override
  protected EvaluationResult validateResult(ProgrammingExercise exercise, ITestData testData, String toEvaluate,
      Object realResult, Object awaitedResult, String output) {
    // TODO Auto-generated method stub
    return new ExecutionResult("Awaited: " + awaitedResult, Success.NONE, toEvaluate, "Gotten: " + realResult, output);
  }
  
}