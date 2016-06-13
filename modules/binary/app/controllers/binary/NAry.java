package controllers.binary;

import model.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

import controllers.core.UserManagement;
import views.html.naryquestion;
import views.html.narysolution;
import model.NAryNumbers.*;

@Security.Authenticated(Secured.class)
public class NAry extends Controller {
  
  @Inject
  FormFactory factory;
  
  NAryConvertionQuestion question;
  
  String learnerSolution;
  
  public Result addLearnerSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    learnerSolution = dynFormula.get("learnerSolution");
    return redirect(routes.NAry.checkSolution());
  }
  
  public Result index() {
    question = new NAryConvertionQuestion();
   	return ok(naryquestion.render(UserManagement.getCurrentUser(),
   			question.getFromNumberType(), question.getFromValue(), question.getToNumberType()));
   	}
  
  public Result checkSolution() {
	return ok(narysolution.render(UserManagement.getCurrentUser(), learnerSolution,
			question.getFromNumberType(), question.getFromValue(),
			question.getToNumberType(), question.getToValue(),
			question.getToValue().equals(learnerSolution)));
  }
}
