package model.html.task;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.HtmlExercise;
import model.html.result.AttributeResult;
import model.html.result.ElementResult;

@Entity
public class CssTask extends Model implements Task {
  
  public static final String MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER = ":";
  public static final String KEY_VALUE_CHARACTER = "=";
  
  @EmbeddedId
  public TaskKey key;
  
  @ManyToOne
  @JoinColumn(name = "exercise_id")
  @JsonBackReference
  public HtmlExercise exercise;
  
  @Column(name = "taskDesc", columnDefinition = "text")
  @JsonIgnore
  public String taskDescription;
  
  @JsonIgnore
  public String xpathQueryName;
  
  public String definingAttribute;
  
  @JsonIgnore
  public String attributes;
  
  @Override
  public ElementResult evaluate(SearchContext searchContext) {
    String xpathQuery = buildXPathQuery();
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));
    
    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE);
    
    boolean allSuccesful = true;
    
    // FIXME: evaluate attributes on all elements...
    for(WebElement element: foundElements) {
      List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(element);
      if(!allResultsSuccessful(evaluatedAttributeResults))
        allSuccesful = false;
    }
    
    if(allSuccesful)
      return new ElementResult(this, Success.COMPLETE);
    else
      return new ElementResult(this, Success.PARTIALLY);
  }
  
  @Override
  public String getDescription() {
    return taskDescription;
  }
  
  @Override
  public int getId() {
    return key.taskId;
  }
  
  private boolean allResultsSuccessful(List<? extends EvaluationResult> results) {
    return results.stream().mapToInt(result -> result.getSuccess() == Success.COMPLETE ? 0 : 1).sum() == 0;
  }
  
  private String buildXPathQuery() {
    String xpathQuery = xpathQueryName;
    
    // Kein Attribut, wenn Tag eindeutig
    if(definingAttribute == null || definingAttribute.isEmpty())
      return xpathQuery;
    
    String[] valueAndKey = definingAttribute.split(KEY_VALUE_CHARACTER);
    xpathQuery += "[@" + valueAndKey[0] + " = '" + valueAndKey[1] + "']";
    return xpathQuery;
  }
  
  private List<AttributeResult> evaluateAllAttributeResults(WebElement foundElement) {
    List<AttributeResult> attributeResults = getAttributeResults();
    attributeResults.forEach(attributeResult -> attributeResult.evaluate(foundElement));
    return attributeResults;
  }
  
  private List<AttributeResult> getAttributeResults() {
    List<AttributeResult> attributesToFind = new LinkedList<AttributeResult>();
    for(String attribute: attributes.split(MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER)) {
      if(!attribute.isEmpty() && attribute.contains(KEY_VALUE_CHARACTER)) {
        String[] valueAndKey = attribute.split(KEY_VALUE_CHARACTER);
        attributesToFind.add(new AttributeResult(valueAndKey[0], valueAndKey[1]));
      }
    }
    return attributesToFind;
  }
  
  protected List<WebElement> filterElementsForTagName(List<WebElement> foundElements, String tagName) {
    return foundElements.parallelStream().filter(element -> element.getTagName().equals(tagName))
        .collect(Collectors.toList());
  }
}
