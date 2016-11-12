package model.logging;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.result.EvaluationResult;
import play.mvc.Http.Request;

public class ExerciseCorrectionEvent extends WorkingEvent {

  private List<EvaluationResult> results;

  public ExerciseCorrectionEvent(Request theRequest, List<EvaluationResult> theResults) {
    super(EventType.EXERCISE_CORRECTION, theRequest);
    results = theResults;
  }

  @Override
  public String getExerciseIdentifier() {
    // FIXME Auto-generated method stub
    return null;
  }

  @JsonIgnore
  public List<EvaluationResult> getResults() {
    return results;
  }

}
