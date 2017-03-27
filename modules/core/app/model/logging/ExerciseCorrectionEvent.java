package model.logging;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCorrectionEvent extends WorkingEvent {
  
  private List<? extends EvaluationResult> results;
  
  public ExerciseCorrectionEvent(Request theRequest, int id, List<? extends EvaluationResult> theResult) {
    super(EventType.EXERCISE_CORRECTION, theRequest, id);
    results = theResult;
  }
  
  @JsonIgnore
  public List<? extends EvaluationResult> getResult() {
    return results;
  }
  
}
