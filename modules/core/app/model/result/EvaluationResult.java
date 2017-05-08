package model.result;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public abstract class EvaluationResult {

  // FIXME: implement builder?
  
  protected FeedbackLevel minimalFL;

  protected FeedbackLevel requestedFL;

  protected Success success = Success.NONE;

  // FIXME: implement feedbackLevel!
  protected List<String> messages = new LinkedList<>();

  public static <T extends EvaluationResult> boolean allResultsSuccessful(List<T> results) {
    return results.stream().filter(EvaluationResult::isSuccessful).count() == results.size();
  }
  
  public static String concatCodeElements(List<String> elements) {
    if(elements.isEmpty())
      return "--";
    return "<code>" + String.join("</code>, <code>", elements) + "</code>";
  }

  public static String concatListElements(List<String> elements) {
    return "<ul><li>" + String.join("</li><li>", elements) + "</li></ul>";
  }
  
  public EvaluationResult(FeedbackLevel theMinimalFL, Success theSuccess, List<String> theMessages) {
    minimalFL = theMinimalFL;
    success = theSuccess;
    messages = theMessages;
  }

  public EvaluationResult(FeedbackLevel theMinimalFL, Success theSuccess, String... theMessages) {
    minimalFL = theMinimalFL;
    success = theSuccess;
    Collections.addAll(messages, theMessages);
  }

  public String getBSClass() {
    return success.getColor();
  }

  public List<String> getMessages() {
    return messages;
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

  public boolean isSuccessful() {
    return success == Success.COMPLETE;
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
