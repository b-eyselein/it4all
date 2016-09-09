package model.javascript.web;

import static model.html.task.Task.allResultsSuccessful;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.openqa.selenium.WebDriver;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Success;

@Entity
public class JsWebTest extends Model {

  private static List<ConditionResult> evaluateConditions(WebDriver driver, List<Condition> conditions,
      boolean isPrecondition) {
    return conditions.stream().map(cond -> cond.test(driver, isPrecondition)).collect(Collectors.toList());
  }

  @Id
  public int id;

  @ManyToOne
  @JsonBackReference
  public JsWebExercise exercise;

  @OneToMany(mappedBy = "pre")
  @JsonManagedReference
  public List<Condition> preconditions;

  @Embedded
  public Action action;

  @OneToMany(mappedBy = "post")
  @JsonManagedReference
  public List<Condition> postconditions;

  public JsWebTestResult test(WebDriver driver) {
    List<ConditionResult> preconditionsSatisfied = evaluateConditions(driver, preconditions, true);

    boolean actionPerformed = action == null || action.perform(driver);

    List<ConditionResult> postconditionSatisfied = evaluateConditions(driver, postconditions, false);

    if(!allResultsSuccessful(preconditionsSatisfied) || !actionPerformed
        || !allResultsSuccessful(postconditionSatisfied))
      return new JsWebTestResult(this, Success.NONE, preconditionsSatisfied, postconditionSatisfied);

    return new JsWebTestResult(this, Success.COMPLETE, preconditionsSatisfied, postconditionSatisfied);
  }

}
