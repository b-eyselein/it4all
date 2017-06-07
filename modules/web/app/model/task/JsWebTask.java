package model.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.openqa.selenium.SearchContext;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.ebean.Finder;
import model.result.ConditionResult;
import model.result.JsWebResult;
import model.result.JsWebResultBuilder;

@Entity
public class JsWebTask extends Task<JsWebResult> {

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
  public JsWebResult evaluate(SearchContext context) {
    // @formatter:off
    return new JsWebResultBuilder(this)
        .withPreResults(evaluateConditions(context, getPreconditions()))
        .withActionPerformed(action == null || action.perform(context))
        .withPostResults(evaluateConditions(context, getPostConditions()))
        .build();
    // @formatter:on
  }

  public void saveInDB() {
    save();
    conditions.forEach(Condition::save);
  }

  private List<Condition> getPostConditions() {
    return conditions.stream().filter(Condition::isPostcondition).collect(Collectors.toList());
  }

  private List<Condition> getPreconditions() {
    return conditions.stream().filter(Condition::isPrecondition).collect(Collectors.toList());
  }

}
