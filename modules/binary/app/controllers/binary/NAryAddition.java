package controllers.binary;

import model.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

import controllers.core.UserManagement;
import views.html.naryadditionquestion;
import views.html.naryadditionsolution;
import model.NAryNumbers.*;
import model.NAryNumbers.Questions.NAryAdditionQuestion;

@Security.Authenticated(Secured.class)
public class NAryAddition extends Controller {
  
  @Inject
  FormFactory factory;
  
  NAryAdditionQuestion question;
  
  String learnerSolution;
  
  public Result addLearnerSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    learnerSolution = new StringBuilder(dynFormula.get("learnerSolution")).reverse().toString();
    return redirect(routes.NAryAddition.checkSolution());
  }
  
  public Result index() {
    question = new NAryAdditionQuestion();
   	return ok(naryadditionquestion.render(UserManagement.getCurrentUser(),
   			question.getNumber1(), question.getNumber2(), question.getNumberType()));
   	}
  
  public Result checkSolution() {
	return ok(naryadditionsolution.render(UserManagement.getCurrentUser(),
			learnerSolution, question.getNumberType(),
			question.getNumber1(), question.getNumber2(), question.getSum(),
			question.getSum().equals(learnerSolution)));
  }
}

