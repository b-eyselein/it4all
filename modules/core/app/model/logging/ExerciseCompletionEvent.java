package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.ExerciseIdentifier;
import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCompletionEvent extends WorkingEvent {
  
  private EvaluationResult result;
  
  public ExerciseCompletionEvent(Request theRequest, ExerciseIdentifier theExerciseIdentifier,
      EvaluationResult theResult) {
    super(EventType.EXERCISE_COMPLETION, theRequest, theExerciseIdentifier);
    result = theResult;
  }

  @JsonIgnore
  public EvaluationResult getResults() {
    return result;
  }
}
