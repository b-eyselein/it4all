package model.html.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.html.HtmlExercise;
import model.html.result.AttributeResult;
import model.html.result.ChildResult;
import model.html.result.ElementResult;
import model.html.result.Success;

@Entity
public class Task extends Model {
  
  public static final String MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER = ":";
  public static final String KEY_VALUE_CHARACTER = "=";

  @EmbeddedId
  public TaskKey key;

  @ManyToOne
  @JoinColumn(name = "exercise_id")
  @JsonBackReference
  public HtmlExercise exercise;

  @Column(name = "taskDesc", length = 2000)
  @JsonIgnore
  public String taskDescription;

  @JsonIgnore
  public String xpathQueryName;

  public String definingAttribute;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  @JsonManagedReference
  @JsonIgnore
  public List<ChildTask> childTasks;

  @JsonIgnore
  public String attributes;

  public ElementResult evaluate(SearchContext searchContext) {
    String xpathQuery = buildXPathQuery();
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));

    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList(),
          "Kein passendes Element gefunden!");
    
    // TODO: evtl. Fehler?
    if(foundElements.size() > 1)
      return new ElementResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList(),
          "Mehr als ein passendes Element gefunden!");
    
    // Nur noch ein passendes Element
    WebElement foundElement = foundElements.get(0);

    // Evaluate ChildResults!
    List<ChildResult> childResults = evaluateAllChildResults(foundElement);

    List<AttributeResult> attributeResults = evaluateAllAttributes(foundElement);

    if(allAttributesFound(attributeResults) && allChildElementsFound(childResults))
      return new ElementResult(this, Success.COMPLETE, attributeResults, childResults, "TODO!");
    else
      return new ElementResult(this, Success.PARTIALLY, attributeResults, childResults, "TODO!");
    
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

  private List<AttributeResult> evaluateAllAttributes(WebElement foundElement) {
    List<AttributeResult> attributeResults = getAttributeResults();
    attributeResults.forEach(attributeResult -> attributeResult.evaluate(foundElement));
    return attributeResults;
  }

  private List<ChildResult> evaluateAllChildResults(WebElement foundElement) {
    List<ChildResult> childResults = getChildResults();
    childResults.forEach(childResult -> childResult.evaluate(foundElement));
    return childResults;
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

  private List<ChildResult> getChildResults() {
    return childTasks.stream().map(childTask -> childTask.getChildResult()).collect(Collectors.toList());
  }

  protected boolean allAttributesFound(List<AttributeResult> attributeResults) {
    return attributeResults.stream().mapToInt(result -> (result.getSuccess() == Success.COMPLETE) ? 0 : 1).sum() == 0;
  }

  protected boolean allChildElementsFound(List<ChildResult> childResults) {
    return childResults.stream().mapToInt(result -> (result.getSuccess() == Success.COMPLETE) ? 0 : 1).sum() == 0;
  }

  protected List<WebElement> filterElementsForTagName(List<WebElement> foundElements, String tagName) {
    return foundElements.parallelStream().filter(element -> element.getTagName().equals(tagName))
        .collect(Collectors.toList());
  }
}
