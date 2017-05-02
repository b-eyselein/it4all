package model.result;

import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class EvaluationFailed extends EvaluationResult {
  
  public EvaluationFailed(String... theMessages) {
    // TODO: FeedbackLevel?!?
    super(FeedbackLevel.NO_FEEDBACK, Success.NONE, theMessages);

  }
  
  public String getAsHtml() {
    return "<div class=\"alert alert-danger\">"
        + messages.stream().collect(Collectors.joining("</p><p>", "<p>", "</p>")) + "</div>";
  }
  
}
