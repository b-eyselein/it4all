package model;

import model.exercise.Success;
import model.programming.ExecutionResult;
import model.programming.ITestData;
import model.programming.ProgLangCorrector;
import model.result.EvaluationResult;

public class PythonCorrector extends ProgLangCorrector<PythonExercise> {
  
  public PythonCorrector() {
    super("python");
  }
  
  @Override
  protected EvaluationResult validateResult(PythonExercise exercise, ITestData testData, String toEvaluate,
      Object realResult, Object awaitedResult, String output) {
    // TODO Auto-generated method stub
    return new ExecutionResult("Awaited: " + awaitedResult, Success.NONE, toEvaluate, "Gotten: " + realResult, output);
  }
  
}
