package model.exercise;

import java.util.List;

public abstract class EvaluationResult {

  protected static String concatCodeElements(List<String> elements) {
    return "<code>" + String.join("</code>, <code>", elements) + "</code>";
  }

  protected static String concatListElements(List<String> elements) {
    return "<ul><li>" + String.join("</li><li>", elements) + "</li></ul>";
  }

  protected Success success = Success.NONE;

  public EvaluationResult(Success theSuccess) {
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

}
