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
import model.NAryNumbers.Questions.NAryConvertionQuestion;

@Security.Authenticated(Secured.class)
public class NAryAddition extends Controller {
  
  @Inject
  FormFactory factory;
  
  public Result index() {
   	return ok(naryadditionquestion.render(UserManagement.getCurrentUser(),
   			new NAryAdditionQuestion()));
   	}
  
  public Result checkSolution() {
	DynamicForm dynFormula = factory.form().bindFromRequest();
	String learnerSolution = new StringBuilder(dynFormula.get("learnerSolution")).reverse().toString().replaceAll("\\s","");
	String[] questionString = dynFormula.get("question").split(",");
	return ok(naryadditionsolution.render(UserManagement.getCurrentUser(),
			new NAryAdditionQuestion(Integer.parseInt(questionString[0]), Integer.parseInt(questionString[1]),
					questionString[2], learnerSolution)));
  }
}

