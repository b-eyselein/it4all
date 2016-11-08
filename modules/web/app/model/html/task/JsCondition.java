package model.html.task;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.result.JsConditionResult;

@Entity
@Table(name = "js_conditions")
public class JsCondition {
  
  // @Id
  // public int id;

  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsTask pre;
  
  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsTask post;

  @JsonIgnore
  public String xpathQuery;
  
  @JsonIgnore
  public String awaitedValue;

  public EvaluationResult evaluate(SearchContext context) {
    WebElement element = context.findElement(By.xpath("todo")); // xpathQuery));

    if(element == null)
      return new JsConditionResult(Success.NONE, this, "", true);

    String gottenValue = element.getText();

    Success success = Success.NONE;
    if(gottenValue.equals(awaitedValue))
      success = Success.COMPLETE;
    
    // evaluateAllAttributeResults(element);

    return new JsConditionResult(success, this, gottenValue, true);
  }

  public String getDescription() {
    // TODO: au...
    return null;
  }

}
