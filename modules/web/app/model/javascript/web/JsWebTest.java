package model.javascript.web;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.openqa.selenium.WebDriver;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import model.javascript.web.Action.ActionType;

@Entity
public class JsWebTest extends Model {

  @Id
  public int id;

  @ManyToOne
  @JsonBackReference
  public JsWebExercise exercise;

  @Enumerated(EnumType.STRING)
  public ActionType actiontype;
  public String actionElementAsString;
  public String otherActionFeatures;

  @OneToOne
  @JoinColumn(name = "precondition")
  public Requirement precondition;
  private boolean preconditionSatisfied;

  // private Action action;
  private boolean actionPerformed;

  @OneToOne
  @JoinColumn(name = "postcondition")
  private Requirement postcondition;
  private boolean postconditionSatisfied;

  public Action getAction() {
    return Action.instantiate(actiontype, actionElementAsString, otherActionFeatures);
  }

  public boolean getActionPerformed() {
    return actionPerformed;
  }

  public Requirement getPostcondition() {
    return postcondition;
  }

  public boolean getPostconditionSatisfied() {
    return postconditionSatisfied;
  }

  public Requirement getPrecondition() {
    return precondition;
  }

  public boolean getPreconditionSatisfied() {
    return preconditionSatisfied;
  }

  public boolean getSuccessful() {
    return preconditionSatisfied && actionPerformed && postconditionSatisfied;
  }

  public void test(WebDriver driver) {
    Action action = Action.instantiate(actiontype, actionElementAsString, otherActionFeatures);

    if(precondition != null)
      preconditionSatisfied = precondition.test(driver);
    // TODO: Abbruch, wenn Bedingung nicht erfüllt?
    // if(!preconditionSatisfied)
    // return;

    actionPerformed = action.perform(driver);

    if(postcondition != null)
      postconditionSatisfied = postcondition.test(driver);
  }

}
