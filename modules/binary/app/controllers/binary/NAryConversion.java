package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.NAryNumbers.NumberBase;
import model.NAryNumbers.questions.NAryConversionQuestion;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.naryconversionquestion;
import views.html.naryconversionsolution;

/**
 * This is the controller class for the NAryConversion section.
 * ("Umwandeln zwischen verschiedenen Zahlensystemen")
 */
@Security.Authenticated(Secured.class)
public class NAryConversion extends Controller {

  @Inject
  private FormFactory factory;

  /**
   * Retrieves the learners solution from the HTML form,
   * creates and updated conversion question and
   * redirects to the solution view.
   * @return
   */
  public Result checkSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    String learnerSolution = dynFormula.get("learnerSolution").replaceAll("\\s", "");
    String value = dynFormula.get("value");
    int startingNB = Integer.parseInt(dynFormula.get("startingNB"));
    int targetNB = Integer.parseInt(dynFormula.get("targetNB"));

    NAryConversionQuestion question = new NAryConversionQuestion(value, NumberBase.getByBase(startingNB),
        NumberBase.getByBase(targetNB), learnerSolution);

    return ok(naryconversionsolution.render(UserManagement.getCurrentUser(), question));
  }

  /**
   * Creates the index view.
   * Creates a new conversion question.
   * @return
   */
  public Result index() {
    NAryConversionQuestion question = NAryConversionQuestion.generateNew();
    return ok(naryconversionquestion.render(UserManagement.getCurrentUser(), question));
  }
}
