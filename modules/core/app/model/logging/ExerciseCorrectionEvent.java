package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCorrectionEvent extends WorkingEvent {

  private EvaluationResult results;

  public ExerciseCorrectionEvent(Request theRequest, int id, EvaluationResult theResult) {
    super(EventType.EXERCISE_CORRECTION, theRequest, id);
    results = theResult;
  }

  @JsonIgnore
  public EvaluationResult getResult() {
    return results;
  }

}
