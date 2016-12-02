package model.result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.user.Settings;
import model.user.Settings.TODO;

public class CompleteResult extends EvaluationResult {

  private List<EvaluationResult> results;
  private String leanerSolution;

  private TODO todo;

  public CompleteResult(String theLearnerSolution, List<EvaluationResult> theResults) {
    super(FeedbackLevel.NO_FEEDBACK, analyzeResults(theResults));
    results = theResults;
    leanerSolution = theLearnerSolution;
    todo = Settings.TODO.SHOW;
  }

  public CompleteResult(String theLearnerSolution, List<EvaluationResult> theResults, Settings.TODO theTodo) {
    super(FeedbackLevel.NO_FEEDBACK, analyzeResults(theResults));
    results = theResults;
    leanerSolution = theLearnerSolution;
    todo = theTodo;
  }

  private static Success analyzeResults(List<EvaluationResult> theResults) {
    for(EvaluationResult res: theResults)
      if(res.getSuccess() != Success.COMPLETE)
        return Success.NONE;
    return Success.COMPLETE;
  }

  @Override
  public String getAsHtml() {
    if(todo == Settings.TODO.SHOW)
      return results.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining());

    Map<Success, List<EvaluationResult>> theResults = results.stream()
        .collect(Collectors.groupingBy(EvaluationResult::getSuccess));

    List<EvaluationResult> positives = theResults.get(Success.COMPLETE);

    if(positives != null && positives.size() == results.size())
      return "<div class=\"col-md-12\"><div class=\"alert alert-success\"> Sie haben alle " + results.size()
          + " Tests erfolgreich bearbeitet.</div></div>";

    StringBuilder builder = new StringBuilder();
    if(todo == Settings.TODO.AGGREGATE && positives != null)
      // Positive Resultate zusammenfassen, falls AGGREGATE, ansonsten
      // ausblenden
      builder.append("<div class=\"col-md-12\"><div class=\"alert alert-success\">Sie haben " + positives.size()
          + " von " + results.size() + " Tests erfolgreicht bearbeitet.</div></div>");

    List<EvaluationResult> partlies = theResults.get(Success.PARTIALLY);
    if(partlies != null)
      builder.append(partlies.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining()));

    List<EvaluationResult> nones = theResults.get(Success.NONE);
    if(nones != null)
      builder.append(nones.stream().map(EvaluationResult::getAsHtml).collect(Collectors.joining()));

    return builder.toString();
  }

  public String getLearnerSolution() {
    return leanerSolution;
  }

}
