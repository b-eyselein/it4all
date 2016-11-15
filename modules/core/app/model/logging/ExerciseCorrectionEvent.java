package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.ExerciseIdentifier;
import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCorrectionEvent extends WorkingEvent {
  
  private EvaluationResult results;
  
  public ExerciseCorrectionEvent(Request theRequest, ExerciseIdentifier theExerciseIdentifier,
      EvaluationResult theResult) {
    super(EventType.EXERCISE_CORRECTION, theRequest, theExerciseIdentifier);
    results = theResult;
  }
  
  @JsonIgnore
  public EvaluationResult getResult() {
    return results;
  }
  
}
