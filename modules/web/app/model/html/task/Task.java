package model.html.task;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "taskType")
public abstract class Task extends Model {
  
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
  
  @Column(name = "tagName")
  @JsonIgnore
  public String tagName;
  
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
    
    return evaluateMore(foundElements);
  }
  
  private String buildXPathQuery() {
    String xpathQuery = "//" + tagName;
    
    // Kein Attribut bei Title oder Tag
    if(definingAttribute == null || definingAttribute.isEmpty())
      return xpathQuery;
    
    
    String[] valueAndKey = definingAttribute.split(KEY_VALUE_CHARACTER);
    xpathQuery += "[@" + valueAndKey[0] + " = '" + valueAndKey[1] + "']";
    return xpathQuery;
  }
  
  public abstract ElementResult evaluateMore(List<WebElement> foundElements);
  
  public List<AttributeResult> evaluateAllAttributes(WebElement element) {
    List<AttributeResult> results = getAttributeResults();
    results.forEach(result -> result.evaluate(element));
    return results;
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
