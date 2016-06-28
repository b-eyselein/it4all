package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.NAryNumbers.Questions.NAryConvertionQuestion;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.naryconvertionquestion;
import views.html.naryconvertionsolution;

@Security.Authenticated(Secured.class)
public class NAryConvertion extends Controller {

  @Inject
  FormFactory factory;
  
  // FIXME: Übergabe an Client!
  NAryConvertionQuestion question;
  String learnerSolution;

  public Result addLearnerSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    learnerSolution = dynFormula.get("learnerSolution");
    return redirect(routes.NAryConvertion.checkSolution());
  }

  public Result checkSolution() {
    return ok(naryconvertionsolution.render(UserManagement.getCurrentUser(), learnerSolution,
        question.getFromNumberType(), question.getFromValue(), question.getToNumberType(), question.getToValue(),
        question.getToValue().equals(learnerSolution)));
  }

  public Result index() {
    question = new NAryConvertionQuestion();
    return ok(naryconvertionquestion.render(UserManagement.getCurrentUser(), question.getFromNumberType(),
        question.getFromValue(), question.getToNumberType()));
  }
}
