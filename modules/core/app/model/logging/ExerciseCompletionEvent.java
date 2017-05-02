package model.logging;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCompletionEvent extends WorkingEvent {
  
  private List<? extends EvaluationResult> result;
  
  public ExerciseCompletionEvent(Request theRequest, int id, List<? extends EvaluationResult> theResult) {
    super(EventType.EXERCISE_COMPLETION, theRequest, id);
    result = theResult;
  }
  
  @JsonIgnore
  public List<? extends EvaluationResult> getResults() {
    return result;
  }
}
