package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.NAryNumbers.Questions.NAryAdditionQuestion;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.naryadditionquestion;
import views.html.naryadditionsolution;

@Security.Authenticated(Secured.class)
public class NAryAddition extends Controller {
  
  @Inject
  private FormFactory factory;
  
  public Result checkSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    
    String committedLearnerSolution = dynFormula.get("learnerSolution");
    String learnerSolution = new StringBuilder(committedLearnerSolution).reverse().toString().replaceAll("\\s", "");
    String[] questionString = dynFormula.get("question").split(",");
    
    NAryAdditionQuestion question = new NAryAdditionQuestion(Integer.parseInt(questionString[0]),
        Integer.parseInt(questionString[1]), questionString[2], learnerSolution);
    
    return ok(naryadditionsolution.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result index() {
    return ok(naryadditionquestion.render(UserManagement.getCurrentUser(), new NAryAdditionQuestion()));
  }
}
