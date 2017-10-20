package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.CompleteResult;
import play.api.mvc.AnyContent;
import play.api.mvc.Request;

public class ExerciseCompletionEvent extends WorkingEvent {

  private CompleteResult<?> result;

  public ExerciseCompletionEvent(Request<AnyContent> theRequest, int id, CompleteResult<?> theResult) {
    super(EventType.EXERCISE_COMPLETION, theRequest, id);
    result = theResult;
  }

  @JsonIgnore
  public CompleteResult<?> getResults() {
    return result;
  }
}
