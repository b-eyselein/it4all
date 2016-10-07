package model.exercise;

import java.util.List;

public abstract class EvaluationResult {

  protected static String concatCodeElements(List<String> elements) {
    return "<code>" + String.join("</code>, <code>", elements) + "</code>";
  }

  protected static String concatListElements(List<String> elements) {
    return "<ul><li>" + String.join("</li><li>", elements) + "</li></ul>";
  }

  protected FeedbackLevel minimalFL;
  protected FeedbackLevel requestedFL;

  protected Success success = Success.NONE;

  public EvaluationResult(FeedbackLevel theMinimalFL, Success theSuccess) {
    minimalFL = theMinimalFL;
    success = theSuccess;
  }

  public abstract String getAsHtml();

  public String getBSClass() {
    switch(success) {
    case COMPLETE:
      return "success";
    case PARTIALLY:
      return "warning";
    case NONE:
      return "danger";
    default:
      return "info";
    }
  }

  public FeedbackLevel getMinimalFeedbackLevel() {
    return minimalFL;
  }

  public int getPoints() {
    return success.getPoints();
  }

  public Success getSuccess() {
    return success;
  }

  public void setSuccess(Success suc) {
    if(suc == null)
      throw new IllegalArgumentException("Success kann nicht auf \"null\" gesetzt werden!");
    success = suc;
  }

  protected FeedbackLevel getFLToUse() {
    if(minimalFL.compareTo(requestedFL) < 0)
      return minimalFL;
    return requestedFL;
  }

}
