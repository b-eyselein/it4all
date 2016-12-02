package model.result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class CompleteResult extends EvaluationResult {

  private List<EvaluationResult> results;
  private String leanerSolution;

  // FIXME: how to do?
  private boolean cutPositives = true;

  public CompleteResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(FeedbackLevel.NO_FEEDBACK, analyzeResults(theResults));
    results = theResults;
    leanerSolution = theLearnerSolution;
    // TODO Auto-generated constructor stub
  }

  private static Success analyzeResults(List<EvaluationResult> theResults) {
    for(EvaluationResult res: theResults)
      if(res.getSuccess() != Success.COMPLETE)
        return Success.NONE;
    return Success.COMPLETE;
  }

  @Override
  public String getAsHtml() {
    // FIXME: complete result?!?
    if(cutPositives) {
      StringBuilder builder = new StringBuilder();

      Map<Success, List<EvaluationResult>> theResults = results.stream()
          .collect(Collectors.groupingBy(EvaluationResult::getSuccess));

      // Positive Resultate
      List<EvaluationResult> positives = theResults.get(Success.COMPLETE);
      if(positives != null)
        builder.append("<div class=\"col-md-12\"><div class=\"alert alert-success\">" + positives.size() + " von "
            + results.size() + " Tests erfolgreicht bearbeitet.</div></div>");

      List<EvaluationResult> partlies = theResults.get(Success.PARTIALLY);
      if(partlies != null)
        builder.append(partlies.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining()));

      List<EvaluationResult> nones = theResults.get(Success.NONE);
      if(nones != null)
        builder.append(nones.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining()));
      
      return builder.toString();
    } else
      return results.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining());
  }

  public String getLearnerSolution() {
    return leanerSolution;
  }

}
