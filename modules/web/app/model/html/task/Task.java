package model.html.task;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.SearchContext;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.result.AttributeResult;

public interface Task {
  
  public static final String MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER = ";";
  
  public static final String KEY_VALUE_CHARACTER = "=";
  
  public static <T extends EvaluationResult> boolean allResultsSuccessful(List<T> results) {
    return results.stream().mapToInt(result -> result.getSuccess() == Success.COMPLETE ? 0 : 1).sum() == 0;
  }
  
  public static List<AttributeResult> getAttributeResults(String attributes) {
    List<AttributeResult> attributesToFind = new LinkedList<>();
    for(String attribute: attributes.split(MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER)) {
      if(!attribute.isEmpty() && attribute.contains(KEY_VALUE_CHARACTER)) {
        String[] valueAndKey = attribute.split(KEY_VALUE_CHARACTER);
        attributesToFind.add(new AttributeResult(valueAndKey[0], valueAndKey[1]));
      }
    }
    return attributesToFind;
  }
  
  public abstract EvaluationResult evaluate(SearchContext context);
  
  public String getDescription();

  public int getId();

}
