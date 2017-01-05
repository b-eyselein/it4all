package model;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.programming.ProgLangCorrector;
import model.result.EvaluationResult;
import model.result.GenericEvaluationResult;

public class PythonCorrector extends ProgLangCorrector<PythonTestData, PythonExercise> {
  
  public PythonCorrector() {
    super("python");
  }

  @Override
  protected EvaluationResult validateResult(PythonExercise exercise, PythonTestData testData, String toEvaluate,
      Object realResult, Object awaitedResult) {
    // TODO Auto-generated method stub
    return new GenericEvaluationResult(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE, "This is a result...");
  }

}
