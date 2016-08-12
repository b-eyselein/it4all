package model.javascript;

import org.openqa.selenium.WebDriver;

import model.javascript.web.Action;
import model.javascript.web.Action.ClickAction;
import model.javascript.web.Condition;

public class JsWebTest {
  
  private Condition precondition;
  private boolean preconditionSatisfied;
  
  private Action action;
  private boolean actionPerformed;
  
  private Condition postcondition;
  private boolean postconditionSatisfied;
  
  public JsWebTest(String conditionElement, String preCondition, String postCondition, String actionElement) {
    precondition = new Condition(conditionElement, preCondition);
    postcondition = new Condition(conditionElement, postCondition);
    action = new ClickAction(actionElement);
  }
  
  public Action getAction() {
    return action;
  }
  
  public boolean getActionPerformed() {
    return actionPerformed;
  }
  
  public Condition getPostcondition() {
    return postcondition;
  }
  
  public boolean getPostconditionSatisfied() {
    return postconditionSatisfied;
  }
  
  public Condition getPrecondition() {
    return precondition;
  }
  
  public boolean getPreconditionSatisfied() {
    return preconditionSatisfied;
  }

  public boolean getSuccessful() {
    return preconditionSatisfied && actionPerformed && postconditionSatisfied;
  }
  
  public void test(WebDriver driver) {
    preconditionSatisfied = precondition.test(driver);
    // TODO: Abbruch, wenn Bedingung nicht erfüllt?
    // if(!preconditionSatisfied)
    // return;
    
    actionPerformed = action.perform(driver);
    
    postconditionSatisfied = postcondition.test(driver);
  }
  
}
