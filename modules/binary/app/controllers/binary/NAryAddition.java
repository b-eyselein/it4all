package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.NAryNumbers.questions.NAryAdditionQuestion;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.naryadditionquestion;
import views.html.naryadditionsolution;

/**
 * This is the controller class for the NaryAddition section. ("Addition in
 * anderen Zahlensystemen")
 */
@Security.Authenticated(Secured.class)
public class NAryAddition extends Controller {
  
  @Inject
  private FormFactory factory;
  
  /**
   * Retrieves the learners solution from the HTML form, creates and updated
   * addition question and redirects to the solution view.
   * 
   * @return
   */
  public Result checkSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    
    String firstSumNAry = dynFormula.get("summand1");
    String secondSumNAry = dynFormula.get("summand2");
    int base = Integer.parseInt(dynFormula.get("base"));
    
    String committedLearnerSolution = dynFormula.get("learnerSolution");
    // Replace all spaces, reverse to compensate input from right to left!
    String learnerSolInNAry = new StringBuilder(committedLearnerSolution).reverse().toString().replaceAll("\\s", "");
    
    NAryAdditionQuestion question = new NAryAdditionQuestion(firstSumNAry, secondSumNAry, base, learnerSolInNAry);
    return ok(naryadditionsolution.render(UserManagement.getCurrentUser(), question));
  }
  
  /**
   * Creates the index view. Creates a new addition question.
   * 
   * @return
   */
  public Result newAdditionQuestion() {
    NAryAdditionQuestion question = NAryAdditionQuestion.generateNew();
    return ok(naryadditionquestion.render(UserManagement.getCurrentUser(), question));
  }
}
