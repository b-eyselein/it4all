package model.exercise;

public class EvaluationFailed extends EvaluationResult {
  
  private String message;

  public EvaluationFailed(String theMessage) {
    super(Success.NONE);
    message = theMessage;
  }

  @Override
  public String getAsHtml() {
    return "<div class=\"alert alert-danger\">" + message + "</div>";
  }
  
}
