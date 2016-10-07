package model.exercise;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericEvaluationResult extends EvaluationResult {
  
  List<String> messages = new LinkedList<>();
  
  public GenericEvaluationResult(FeedbackLevel theMinimalFL, Success theSuccess, String... theMessages) {
    super(theMinimalFL, theSuccess);
    for(String m: theMessages)
      messages.add(m);
  }
  
  @Override
  public String getAsHtml() {
    return messages.stream().map(m -> "<div class=\"alert alert-" + getBSClass() + "\">" + m + "</div>")
        .collect(Collectors.joining());
  }
  
}
