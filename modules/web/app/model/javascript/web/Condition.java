package model.javascript.web;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import model.exercise.Success;

@Entity
@Table(name = "conditions")
public class Condition extends Model {
  
  @Id
  public int id;
  
  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsWebTest pre;
  
  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsWebTest post;
  
  public String xpathquery;
  
  public String awaitedvalue;
  
  public String getDescription() {
    return "Element mit XPath \"" + xpathquery + "\" sollte den Inhalt \"" + awaitedvalue + "\" haben";
  }
  
  public ConditionResult test(WebDriver driver, boolean isPrecondition) {
    WebElement element = driver.findElement(By.xpath(xpathquery));
    
    String gottenValue = element.getText();
    
    if(element == null || !gottenValue.equals(awaitedvalue))
      return new ConditionResult(Success.NONE, this, gottenValue, isPrecondition);
    
    return new ConditionResult(Success.COMPLETE, this, gottenValue, isPrecondition);
  }
}
