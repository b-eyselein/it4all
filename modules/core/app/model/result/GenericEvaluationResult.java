package model.result;

import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class GenericEvaluationResult extends EvaluationResult {
  
  public GenericEvaluationResult(FeedbackLevel theMinimalFL, Success theSuccess, String... theMessages) {
    super(theMinimalFL, theSuccess);
    for(String m: theMessages)
      messages.add(m);
  }
  
  public String getAsHtml() {
    return "<div class=\"col-md-12\">" + messages.stream()
        .map(m -> "<div class=\"alert alert-" + getBSClass() + "\">" + m + "</div>").collect(Collectors.joining())
        + "</div>";
  }
  
}
