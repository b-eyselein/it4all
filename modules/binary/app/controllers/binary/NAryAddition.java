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
  FormFactory factory;

  // FIXME: Übergabe an Client!
  NAryAdditionQuestion question;
  String learnerSolution;
  
  public Result addLearnerSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    learnerSolution = dynFormula.get("learnerSolution");
    return redirect(routes.NAryAddition.checkSolution());
  }
  
  public Result checkSolution() {
    return ok(naryadditionsolution.render(UserManagement.getCurrentUser(), learnerSolution, question.getNumberType(),
        question.getNumber1(), question.getNumber2(), question.getSum(), question.getSum().equals(learnerSolution)));
  }
  
  public Result index() {
    question = new NAryAdditionQuestion();
    return ok(naryadditionquestion.render(UserManagement.getCurrentUser(), question.getNumber1(), question.getNumber2(),
        question.getNumberType()));
  }
}
