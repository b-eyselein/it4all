package model.javascript.web;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Requirement extends Model {

  @Id
  public int id;

  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsWebTest pre;
  
  @JsonBackReference
  @ManyToOne(cascade = CascadeType.ALL)
  public JsWebTest post;

  public String xpathQuery;

  public String innerHTML;

  public String getDescription() {
    return "Element mit XPath \"" + xpathQuery + "\" sollte inneren Text \"" + innerHTML + "\" haben";
  }

  public boolean test(WebDriver driver) {
    WebElement element = driver.findElement(By.xpath(xpathQuery));

    boolean success = element != null && element.getText().equals(innerHTML);
    return success;
  }

}
