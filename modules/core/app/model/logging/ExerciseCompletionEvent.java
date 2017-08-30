package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.CompleteResult;
import play.mvc.Http.Request;

public class ExerciseCompletionEvent extends WorkingEvent {
  
  private CompleteResult<?> result;
  
  public ExerciseCompletionEvent(Request theRequest, int id, CompleteResult<?> theResult) {
    super(EventType.EXERCISE_COMPLETION, theRequest, id);
    result = theResult;
  }
  
  @JsonIgnore
  public CompleteResult<?> getResults() {
    return result;
  }
}
