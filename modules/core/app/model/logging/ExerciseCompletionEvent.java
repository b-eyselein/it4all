package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCompletionEvent extends WorkingEvent {

  private EvaluationResult result;

  public ExerciseCompletionEvent(Request theRequest, int id, EvaluationResult theResult) {
    super(EventType.EXERCISE_COMPLETION, theRequest, id);
    result = theResult;
  }

  @JsonIgnore
  public EvaluationResult getResults() {
    return result;
  }
}
