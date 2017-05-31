package model.result;

import java.util.List;

import model.exercise.Success;

public class CompleteResult extends EvaluationResult {
  
  private List<EvaluationResult> results;
  private String leanerSolution;
  
  public CompleteResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(analyzeResults(theResults));
    results = theResults;
    leanerSolution = theLearnerSolution;
  }
  
  private static Success analyzeResults(List<EvaluationResult> theResults) {
    for(EvaluationResult res: theResults)
      if(res.getSuccess() != Success.COMPLETE)
        return Success.NONE;
    return Success.COMPLETE;
  }
  
  // public String getAsHtml() {
  // if(todo == SHOW_HIDE_AGGREGATE.SHOW)
  // return
  // results.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining());
  //
  // Map<Success, List<EvaluationResult>> theResults = results.stream()
  // .collect(Collectors.groupingBy(EvaluationResult::getSuccess));
  //
  // List<EvaluationResult> positives = theResults.get(Success.COMPLETE);
  //
  // if(positives != null && positives.size() == results.size())
  // return "<div class=\"col-md-12\"><div class=\"alert alert-success\"> Sie
  // haben alle " + results.size()
  // + " Tests erfolgreich bearbeitet.</div></div>";
  //
  // StringBuilder builder = new StringBuilder();
  // if(todo == SHOW_HIDE_AGGREGATE.AGGREGATE && positives != null)
  // // Positive Resultate zusammenfassen, falls AGGREGATE, ansonsten
  // // ausblenden
  // builder.append("<div class=\"col-md-12\"><div class=\"alert
  // alert-success\">Sie haben " + positives.size()
  // + " von " + results.size() + " Tests erfolgreicht
  // bearbeitet.</div></div>");
  //
  // List<EvaluationResult> partlies = theResults.get(Success.PARTIALLY);
  // if(partlies != null)
  // builder.append(partlies.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining()));
  //
  // List<EvaluationResult> nones = theResults.get(Success.NONE);
  // if(nones != null)
  // builder.append(nones.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining()));
  //
  // return builder.toString();
  // }
  
  public String getLearnerSolution() {
    return leanerSolution;
  }
  
  public List<EvaluationResult> getResults() {
    return results;
  }
  
}
