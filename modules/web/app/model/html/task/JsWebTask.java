package model.html.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.openqa.selenium.SearchContext;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Success;
import model.javascript.Action;
import model.javascript.Condition;
import model.javascript.ConditionResult;
import model.javascript.JsWebTestResult;
import model.result.EvaluationResult;

@Entity
public class JsWebTask extends Task {

  public static final Finder<TaskKey, JsWebTask> finder = new Finder<>(JsWebTask.class);

  @OneToMany(mappedBy = "pre")
  @JsonManagedReference
  public List<Condition> preconditions;
  
  @Embedded
  public Action action;

  @OneToMany(mappedBy = "post")
  @JsonManagedReference
  public List<Condition> postconditions;

  public JsWebTask(TaskKey theKey) {
    super(theKey);
  }

  private static List<ConditionResult> evaluateConditions(SearchContext context, List<Condition> conditions,
      boolean isPrecondition) {
    return conditions.stream().map(cond -> cond.test(context, isPrecondition)).collect(Collectors.toList());
  }

  @Override
  public EvaluationResult evaluate(SearchContext context) {
    List<ConditionResult> preconditionsSatisfied = evaluateConditions(context, preconditions, true);
    
    boolean actionPerformed = action == null || action.perform(context);

    List<ConditionResult> postconditionSatisfied = evaluateConditions(context, postconditions, false);
    
    Success success = Success.NONE;
    if(allResultsSuccessful(preconditionsSatisfied) && !actionPerformed
        && !allResultsSuccessful(postconditionSatisfied))
      success = Success.COMPLETE;

    return new JsWebTestResult(this, success, preconditionsSatisfied, postconditionSatisfied);

  }

}
