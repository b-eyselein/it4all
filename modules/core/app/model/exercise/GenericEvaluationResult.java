package model.exercise;

import java.util.stream.Collectors;

public class GenericEvaluationResult extends EvaluationResult {

  public GenericEvaluationResult(FeedbackLevel theMinimalFL, Success theSuccess, String... theMessages) {
    super(theMinimalFL, theSuccess);
    for(String m: theMessages)
      messages.add(m);
  }
  
  @Override
  public String getAsHtml() {
    return "<div class=\"col-md-12\">" + messages.stream()
        .map(m -> "<div class=\"alert alert-" + getBSClass() + "\">" + m + "</div>").collect(Collectors.joining())
        + "</div>";
  }
  
}
