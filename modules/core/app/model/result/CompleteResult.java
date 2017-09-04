package model.result;

import java.util.List;

import model.exercise.Success;
import play.twirl.api.Html;

public class CompleteResult<E extends EvaluationResult> extends EvaluationResult {

  private final List<E> results;
  private final String learnerSolution;

  public CompleteResult(String theLearnerSolution, List<E> theResults) {
    super(analyzeResults(theResults));
    results = theResults;
    learnerSolution = theLearnerSolution;
  }

  private static <T extends EvaluationResult> Success analyzeResults(List<T> theResults) {
    if(theResults.stream().anyMatch(res -> res.getSuccess() != Success.COMPLETE))
      return Success.NONE;
    else
      return Success.COMPLETE;
  }

  public String getLearnerSolution() {
    return learnerSolution;
  }

  public List<E> getResults() {
    return results;
  }

  public Html renderLearnerSolution() {
    return views.html.learnerSol.render(learnerSolution);
  }

}
