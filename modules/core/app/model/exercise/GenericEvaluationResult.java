package model.exercise;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericEvaluationResult extends EvaluationResult {

  // FIXME: implement feedbackLevel!
  List<String> messages = new LinkedList<>();
  
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
