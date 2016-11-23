package model.html.task;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Success;
import model.html.WebExercise;
import model.html.result.AttributeResult;
import model.result.EvaluationResult;

@MappedSuperclass
public abstract class Task {
  
  public static final String MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER = ";";
  
  public static final String KEY_VALUE_CHARACTER = "=";
  
  @EmbeddedId
  public TaskKey key;

  @ManyToOne
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  @JsonBackReference
  public WebExercise exercise;
  
  @Column(columnDefinition = "text")
  @JsonIgnore
  public String text;
  
  @JsonIgnore
  public String xpathQuery;
  
  @JsonIgnore
  public String attributes;
  
  public Task(TaskKey theKey) {
    key = theKey;
  }
  
  public static <T extends EvaluationResult> boolean allResultsSuccessful(List<T> results) {
    for(EvaluationResult res: results)
      if(res.getSuccess() != Success.COMPLETE)
        return false;
    return true;
  }
  
  public abstract EvaluationResult evaluate(SearchContext context);
  
  public String getDescription() {
    return text;
  }
  
  public int getId() {
    return key.taskId;
  }

  protected List<AttributeResult> evaluateAllAttributeResults(WebElement foundElement) {
    List<AttributeResult> attributeResults = getAttributeResults();
    attributeResults.forEach(attributeResult -> attributeResult.evaluate(foundElement));
    return attributeResults;
  }
  
  protected List<AttributeResult> getAttributeResults() {
    List<AttributeResult> attributesToFind = new LinkedList<>();
    for(String attribute: attributes.split(MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER)) {
      if(!attribute.isEmpty() && attribute.contains(KEY_VALUE_CHARACTER)) {
        String[] valueAndKey = attribute.split(KEY_VALUE_CHARACTER);
        attributesToFind.add(new AttributeResult(valueAndKey[0], valueAndKey[1]));
      }
    }
    return attributesToFind;
  }
  
}
