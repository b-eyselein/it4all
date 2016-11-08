package model.html.task;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.openqa.selenium.SearchContext;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.EvaluationResult;
import model.html.WebExercise;

@Entity
public class JsTask extends Model implements Task {
  
  public static final Finder<TaskKey, JsTask> finder = new Finder<>(JsTask.class);
  
  @OneToMany(mappedBy = "pre")
  @JsonManagedReference
  public List<JsCondition> preconditions;
  
  @EmbeddedId
  public TaskKey key;
  
  @ManyToOne
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  @JsonBackReference
  public WebExercise exercise;
  
  @Column(columnDefinition = "text")
  @JsonIgnore
  public String text;
  
  @Embedded
  public JsAction action;
  
  @OneToMany(mappedBy = "post")
  @JsonManagedReference
  public List<JsCondition> postconditions;
  
  public JsTask(TaskKey theKey) {
    key = theKey;
  }
  
  @Override
  public EvaluationResult evaluate(SearchContext context) {
    // TODO Auto-generated method stub
    // List<ConditionResult> preconditionsSatisfied = evaluateConditions(driver,
    // preconditions, true);
    //
    // boolean actionPerformed = action == null || action.perform(driver);
    //
    // List<ConditionResult> postconditionSatisfied = evaluateConditions(driver,
    // postconditions, false);
    //
    // if(!allResultsSuccessful(preconditionsSatisfied) || !actionPerformed
    // || !allResultsSuccessful(postconditionSatisfied))
    // return new JsWebTestResult(this, Success.NONE, preconditionsSatisfied,
    // postconditionSatisfied);
    //
    // return new JsWebTestResult(this, Success.COMPLETE,
    // preconditionsSatisfied, postconditionSatisfied);
    return null;
  }
  
  @Override
  public String getDescription() {
    return text;
  }
  
  @Override
  public int getId() {
    return key.taskId;
  }
  
}
