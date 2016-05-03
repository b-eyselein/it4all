package model.xml.task;

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

import model.html.HtmlExercise;
import model.html.result.AttributeResult;
import model.html.task.ChildTask;
import model.html.result.ElementResult;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "taskType")
public abstract class Task extends Model {
  
  public static final String SPLIT_CHARACTER = ":";
  public static final String KEY_VALUE_CHARACTER = "=";
  
  @EmbeddedId
  public TaskKey key;
  
  @ManyToOne
  @JoinColumn(name = "exercise_id")
  public HtmlExercise exercise;
  
  @Column(name = "taskDesc", length = 2000)
  public String taskDescription;
  
  @Column(name = "tagName")
  public String tagName;
  
  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  public List<ChildTask> childTasks;
  
  public String attributes;
  
  public abstract ElementResult<? extends Task> evaluate(SearchContext searchContext);
  
  public List<AttributeResult> evaluateAllAttributes(WebElement element) {
    List<AttributeResult> results = getAttributeResults();
    results.forEach(result -> result.evaluate(element));
    return results;
  }
  
  private List<AttributeResult> getAttributeResults() {
    List<AttributeResult> attributesToFind = new LinkedList<AttributeResult>();
    for(String attribute: attributes.split(SPLIT_CHARACTER)) {
      if(!attribute.isEmpty() && attribute.contains(KEY_VALUE_CHARACTER)) {
        String[] valueAndKey = attribute.split(KEY_VALUE_CHARACTER);
        attributesToFind.add(new AttributeResult(valueAndKey[0], valueAndKey[1]));
      }
    }
    return attributesToFind;
  }
  
  protected boolean allAttributesFound(List<AttributeResult> results) {
    return results.stream().mapToInt(result -> result.isFound() ? 0 : 1).sum() == 0;
  }
  
  protected List<WebElement> filterElementsForTagName(List<WebElement> foundElements, String tagName) {
    return foundElements.parallelStream().filter(element -> element.getTagName().equals(tagName))
        .collect(Collectors.toList());
  }
}
