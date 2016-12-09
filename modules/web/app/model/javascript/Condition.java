package model.javascript;

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
import model.html.task.JsWebTask;
import play.Logger;

@Entity
@Table(name = "conditions")
public class Condition extends Model {
  
  public static final Finder<JsConditionKey, Condition> finder = new Finder<>(Condition.class);
  
  @EmbeddedId
  public JsConditionKey key;
  
  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumns({@JoinColumn(name = "task_id", referencedColumnName = "task_id", insertable = false, updatable = false),
      @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", insertable = false, updatable = false)})
  public JsWebTask task;
  
  public String xpathquery; // NOSONAR
  
  public String awaitedvalue; // NOSONAR
  
  public boolean isPrecond; // NOSONAR
  
  public Condition(JsConditionKey theKey) {
    key = theKey;
  }
  
  public String getDescription() {
    return "Element mit XPath \"" + xpathquery + "\" sollte den Inhalt \"" + awaitedvalue + "\" haben";
  }
  
  public boolean isPostcondition() {
    return !isPrecond;
  }
  
  public boolean isPrecondition() {
    return isPrecond;
  }
  
  public ConditionResult test(SearchContext context) {
    WebElement element = context.findElement(By.xpath(xpathquery));
    
    if(element == null)
      return new ConditionResult(Success.NONE, this, "", isPrecond);
    
    String gottenValue = element.getText();
    
    Success success = Success.NONE;
    if(gottenValue.equals(awaitedvalue))
      success = Success.COMPLETE;
    
    Logger.debug(isPrecond + " :: " + gottenValue);
    
    return new ConditionResult(success, this, gottenValue, isPrecond);
  }
}
