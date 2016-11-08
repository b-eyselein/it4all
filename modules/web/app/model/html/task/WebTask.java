package model.html.task;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import model.html.WebExercise;
import model.html.result.AttributeResult;

@MappedSuperclass
public abstract class WebTask extends Model implements Task {
  
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

  public WebTask(TaskKey theKey) {
    key = theKey;
  }

  @Override
  public String getDescription() {
    return text;
  }
  
  @Override
  public int getId() {
    return key.taskId;
  }
  
  protected List<AttributeResult> evaluateAllAttributeResults(WebElement foundElement) {
    List<AttributeResult> attributeResults = Task.getAttributeResults(attributes);
    attributeResults.forEach(attributeResult -> attributeResult.evaluate(foundElement));
    return attributeResults;
  }

}
