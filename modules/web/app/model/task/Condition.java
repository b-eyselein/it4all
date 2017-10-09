package model.task;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
@Table(name = "conditions")
public class Condition extends Model {

  public static final Finder<JsConditionKey, Condition> finder = new Finder<>(Condition.class);

  @EmbeddedId
  public JsConditionKey key;

  @ManyToOne
  @JoinColumn(name = "task_id", referencedColumnName = "task_id", insertable = false, updatable = false)
  @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", insertable = false, updatable = false)
  @JsonBackReference
  public JsWebTask task;

  @Column
  public String xpathQuery;

  @Column
  public boolean isPrecondition;

  @Column
  public String awaitedValue;

  public Condition(JsConditionKey theKey) {
    key = theKey;
  }

  @JsonIgnore
  public String getDescription() {
    return "Element mit XPath \"" + xpathQuery + "\" sollte den Inhalt \"" + awaitedValue + "\" haben";
  }

  @JsonIgnore
  public boolean isPostcondition() {
    return !isPrecondition;
  }

  @JsonIgnore
  public boolean isPrecondition() {
    return isPrecondition;
  }

}
