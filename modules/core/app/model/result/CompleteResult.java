package model.result;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public abstract class CompleteResult extends EvaluationResult {
  
  private List<EvaluationResult> results;
  private String leanerSolution;
  
  public CompleteResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(FeedbackLevel.NO_FEEDBACK, analyzeResults(theResults));
    results = theResults;
    leanerSolution = theLearnerSolution;
    // TODO Auto-generated constructor stub
  }
  
  private static Success analyzeResults(List<EvaluationResult> theResults) {
    // FIXME: implement! Auto-generated method stub
    Success success = Success.COMPLETE;
    for(EvaluationResult res: theResults)
      if(res.getSuccess() == Success.NONE || res.getSuccess() == Success.PARTIALLY)
        success = Success.NONE;
    return success;
  }
  
  @Override
  public String getAsHtml() {
    // FIXME: complete result?!?
    return results.stream().map(r -> r.getAsHtml()).collect(Collectors.joining());
  }

  public String getLearnerSolution() {
    return leanerSolution;
  }

}
