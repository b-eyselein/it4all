package model.task;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import model.exercise.Success;
import model.result.ConditionResult;

@Entity
@Table(name = "conditions")
public class Condition extends Model {

  public static final Finder<JsConditionKey, Condition> finder = new Finder<>(Condition.class);

  @EmbeddedId
  public JsConditionKey key;

  @ManyToOne(cascade = CascadeType.ALL)
  @JsonBackReference
  @JoinColumns({@JoinColumn(name = "task_id", referencedColumnName = "task_id", insertable = false, updatable = false),
      @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", insertable = false, updatable = false)})
  public JsWebTask task;

  public String xpathQuery;

  public String awaitedvalue;

  public boolean isPrecondition;

  public Condition(JsConditionKey theKey) {
    key = theKey;
  }

  public String getDescription() {
    return "Element mit XPath \"" + xpathQuery + "\" sollte den Inhalt \"" + awaitedvalue + "\" haben";
  }

  public boolean isPostcondition() {
    return !isPrecondition;
  }

  public boolean isPrecondition() {
    return isPrecondition;
  }

  public ConditionResult test(SearchContext context) {
    WebElement element = context.findElement(By.xpath(xpathQuery));

    if(element == null)
      return new ConditionResult(Success.NONE, this, "", isPrecondition);

    String gottenValue = element.getText();

    Success success = Success.NONE;
    if(gottenValue.equals(awaitedvalue))
      success = Success.COMPLETE;

    return new ConditionResult(success, this, gottenValue, isPrecondition);
  }
}
