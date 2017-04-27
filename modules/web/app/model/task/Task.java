package model.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.Splitter;

import model.Attribute;
import model.WebExercise;
import model.exercise.Success;
import model.result.AttributeResult;
import model.result.ElementResult;
import model.result.EvaluationResult;

@MappedSuperclass
public abstract class Task extends Model {

  private static final Splitter SPLITTER = Splitter.on(";").omitEmptyStrings();

  @EmbeddedId
  public TaskKey key;

  @ManyToOne
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  @JsonBackReference
  public WebExercise exercise;

  @Column(columnDefinition = "text")
  public String text;

  public String xpathQuery;

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

  public abstract ElementResult evaluate(SearchContext context);

  public List<Attribute> getAttributes() {
    return SPLITTER.splitToList(attributes).stream().map(Attribute::fromString).collect(Collectors.toList());
  }

  protected List<AttributeResult> evaluateAllAttributeResults(WebElement foundElement) {
    List<AttributeResult> attributeResults = getAttributeResults();
    attributeResults.forEach(attributeResult -> attributeResult.evaluate(foundElement));
    return attributeResults;
  }
  
  protected List<AttributeResult> getAttributeResults() {
    return getAttributes().stream().map(AttributeResult::new).collect(Collectors.toList());
  }

}
