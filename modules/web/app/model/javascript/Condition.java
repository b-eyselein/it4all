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

@Entity
@Table(name = "conditions")
public class Condition extends Model {
  
  @EmbeddedId
  public JsConditionKey id;
  
  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsWebTask pre;
  
  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsWebTask post;
  
  public String xpathquery; // NOSONAR
  
  public String awaitedvalue; // NOSONAR
  
  public String getDescription() {
    return "Element mit XPath \"" + xpathquery + "\" sollte den Inhalt \"" + awaitedvalue + "\" haben";
  }
  
  public ConditionResult test(SearchContext context, boolean isPrecondition) {
    WebElement element = context.findElement(By.xpath(xpathquery));
    
    if(element == null)
      return new ConditionResult(Success.NONE, this, "", isPrecondition);
    
    String gottenValue = element.getText();
    
    Success success = Success.NONE;
    if(gottenValue.equals(awaitedvalue))
      success = Success.COMPLETE;
    
    return new ConditionResult(success, this, gottenValue, isPrecondition);
  }
}
