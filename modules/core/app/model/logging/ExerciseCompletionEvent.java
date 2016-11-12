package model.logging;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCompletionEvent extends WorkingEvent {

  private List<EvaluationResult> results;

  public ExerciseCompletionEvent(Request theRequest, List<EvaluationResult> theResults) {
    super(EventType.EXERCISE_COMPLETION, theRequest);
    results = theResults;
  }

  @Override
  public String getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @JsonIgnore
  public List<EvaluationResult> getResults() {
    return results;
  }
}
