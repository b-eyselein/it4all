package model;

import model.programming.ProgLangCorrector;
import model.result.EvaluationResult;

public class PythonCorrector extends ProgLangCorrector<PythonTestData, PythonExercise> {

  public PythonCorrector() {
    super("python");
  }
  
  @Override
  protected EvaluationResult validateResult(PythonExercise exercise, PythonTestData testData, String toEvaluate,
      Object realResult, Object awaitedResult) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
