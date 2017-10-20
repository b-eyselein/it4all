package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.CompleteResult;
import play.api.mvc.AnyContent;
import play.api.mvc.Request;

public class ExerciseCorrectionEvent extends WorkingEvent {

  private CompleteResult<?> results;

  public ExerciseCorrectionEvent(Request<AnyContent> theRequest, int id, CompleteResult<?> theResult) {
    super(EventType.EXERCISE_CORRECTION, theRequest, id);
    results = theResult;
  }

  @JsonIgnore
  public CompleteResult<?> getResult() {
    return results;
  }

}
