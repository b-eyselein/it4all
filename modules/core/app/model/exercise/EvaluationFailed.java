package model.exercise;

import java.util.stream.Collectors;

public class EvaluationFailed extends EvaluationResult {

  public EvaluationFailed(String... theMessages) {
    // TODO: FeedbackLevel?!?
    super(FeedbackLevel.NO_FEEDBACK, Success.NONE, theMessages);
    
  }

  @Override
  public String getAsHtml() {
    return "<div class=\"alert alert-danger\">"
        + messages.stream().collect(Collectors.joining("</p><p>", "<p>", "</p>")) + "</div>";
  }

}
