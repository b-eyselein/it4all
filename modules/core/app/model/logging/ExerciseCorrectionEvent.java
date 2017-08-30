package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.CompleteResult;
import play.mvc.Http.Request;

public class ExerciseCorrectionEvent extends WorkingEvent {
  
  private CompleteResult<?> results;
  
  public ExerciseCorrectionEvent(Request theRequest, int id, CompleteResult<?> theResult) {
    super(EventType.EXERCISE_CORRECTION, theRequest, id);
    results = theResult;
  }
  
  @JsonIgnore
  public CompleteResult<?> getResult() {
    return results;
  }
  
}
