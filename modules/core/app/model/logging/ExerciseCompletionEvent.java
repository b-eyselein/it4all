package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Exercise;
import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCompletionEvent extends WorkingEvent {

  private EvaluationResult result;

  public ExerciseCompletionEvent(Request theRequest, Exercise theExercise, EvaluationResult theResult) {
    super(EventType.EXERCISE_COMPLETION, theRequest, theExercise);
    result = theResult;
  }
  
  @JsonIgnore
  public EvaluationResult getResults() {
    return result;
  }
}
