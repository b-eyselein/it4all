package model.result;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import model.exercise.Success;

public class EvaluationResult {
  
  // FIXME: implement builder?
  
  protected Success success = Success.NONE;
  
  protected List<String> messages = new LinkedList<>();
  
  public EvaluationResult(Success theSuccess, List<String> theMessages) {
    success = theSuccess;
    messages = theMessages;
  }
  
  public EvaluationResult(Success theSuccess, String... theMessages) {
    success = theSuccess;
    Collections.addAll(messages, theMessages);
  }
  
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
  
  public String getBSClass() {
    return success.getColor();
  }
  
  public String getGlyphicon() {
    switch(success) {
    case COMPLETE:
      return "glyphicon glyphicon-ok";
    case PARTIALLY:
      return "glyphicon glyphicon-question-sign";
    case NONE:
    case FAILURE:
    default:
      return "glyphicon glyphicon-remove";
    }
  }
  
  public List<String> getMessages() {
    return messages;
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
  
}
