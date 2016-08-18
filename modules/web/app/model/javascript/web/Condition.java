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
  
  public String xpathQuery;
  
  public String toEvaluate;
  
  public String getDescription() {
    return "Element mit XPath \"" + xpathQuery + "\" sollte inneren Text \"" + toEvaluate + "\" haben";
  }
  
  public boolean test(WebDriver driver) {
    WebElement element = driver.findElement(By.xpath(xpathQuery));
    return element != null && element.getText().equals(toEvaluate);
  }
}
