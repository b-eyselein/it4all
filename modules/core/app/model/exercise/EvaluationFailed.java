package model.exercise;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluationFailed extends EvaluationResult {

  private List<String> messages = new LinkedList<>();

  public EvaluationFailed(String... aMessage) {
    super(Success.NONE);
    for(String message: aMessage)
      messages.add(message);
  }

  @Override
  public String getAsHtml() {
    return "<div class=\"alert alert-danger\">"
        + messages.stream().collect(Collectors.joining("</p><p>", "<p>", "</p>")) + "</div>";
  }

}
