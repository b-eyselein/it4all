package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.NAryNumbers.questions.NAryConvertionQuestion;
import model.NAryNumbers.questions.NumberBase;
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
  private FormFactory factory;

  public Result checkSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    String learnerSolution = dynFormula.get("learnerSolution").replaceAll("\\s", "");
    String value = dynFormula.get("value");
    int startingNB = Integer.parseInt(dynFormula.get("startingNB"));
    int targetNB = Integer.parseInt(dynFormula.get("targetNB"));

    NAryConvertionQuestion question = new NAryConvertionQuestion(value, NumberBase.getByBase(startingNB),
        NumberBase.getByBase(targetNB), learnerSolution);

    return ok(naryconvertionsolution.render(UserManagement.getCurrentUser(), question));
  }

  public Result index() {
    NAryConvertionQuestion question = NAryConvertionQuestion.generateNew();
    return ok(naryconvertionquestion.render(UserManagement.getCurrentUser(), question));
  }
}
