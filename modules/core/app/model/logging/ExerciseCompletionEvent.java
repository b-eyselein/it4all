package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCompletionEvent extends WorkingEvent {

  private EvaluationResult result;

  public ExerciseCompletionEvent(Request theRequest, EvaluationResult theResult) {
    super(EventType.EXERCISE_COMPLETION, theRequest);
    result = theResult;
  }

  @Override
  public String getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @JsonIgnore
  public EvaluationResult getResults() {
    return result;
  }
}
