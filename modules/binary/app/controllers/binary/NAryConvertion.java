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

  public Result checkSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    String learnerSolution = dynFormula.get("learnerSolution").replaceAll("\\s", "");
    String[] questionString = dynFormula.get("question").split(",");

    NAryConvertionQuestion question = new NAryConvertionQuestion(Integer.parseInt(questionString[0]), questionString[1],
        questionString[2], learnerSolution);

    return ok(naryconvertionsolution.render(UserManagement.getCurrentUser(), question));
  }

  public Result index() {
    return ok(naryconvertionquestion.render(UserManagement.getCurrentUser(), new NAryConvertionQuestion()));
  }
}
