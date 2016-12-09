package model.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.openqa.selenium.SearchContext;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.result.ConditionResult;
import model.result.EvaluationResult;
import model.result.JsWebTestResult;

@Entity
public class JsWebTask extends Task {

  public static final Finder<TaskKey, JsWebTask> finder = new Finder<>(JsWebTask.class);

  @OneToMany(mappedBy = "task")
  @JsonManagedReference
  public List<Condition> conditions;

  @Embedded
  public Action action;

  public JsWebTask(TaskKey theKey) {
    super(theKey);
  }

  private static List<ConditionResult> evaluateConditions(SearchContext context, List<Condition> conditions) {
    return conditions.stream().map(cond -> cond.test(context)).collect(Collectors.toList());
  }

  @Override
  public EvaluationResult evaluate(SearchContext context) {
    List<ConditionResult> preconditionsSatisfied = evaluateConditions(context, getPreconditions());
    boolean actionPerformed = action == null || action.perform(context);
    List<ConditionResult> postconditionSatisfied = evaluateConditions(context, getPostConditions());

    return new JsWebTestResult(this, preconditionsSatisfied, actionPerformed, postconditionSatisfied);

  }

  private List<Condition> getPostConditions() {
    return conditions.stream().filter(Condition::isPostcondition).collect(Collectors.toList());
  }

  private List<Condition> getPreconditions() {
    return conditions.stream().filter(Condition::isPrecondition).collect(Collectors.toList());
  }

}
