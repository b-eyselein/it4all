package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.ExerciseIdentifier;
import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCorrectionEvent extends WorkingEvent {

  private EvaluationResult results;

  public ExerciseCorrectionEvent(Request theRequest, EvaluationResult theResult) {
    super(EventType.EXERCISE_CORRECTION, theRequest);
    results = theResult;
  }

  @Override
  public ExerciseIdentifier getExerciseIdentifier() {
    // FIXME Auto-generated method stub
    return null;
  }

  @JsonIgnore
  public EvaluationResult getResult() {
    return results;
  }

}
