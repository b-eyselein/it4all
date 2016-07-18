package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.bool.*;
import model.bool.tree.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.boolquestion;
import views.html.boolsolution;

@Security.Authenticated(Secured.class)
public class Bool extends Controller {

  @Inject
  private FormFactory factory;

  public Result indexSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    BoolescheFunktionTree bft = BoolescheFunktionParser.parse(dynFormula.get("bft"));
    double d = bft.getAnzahlVariablen();
    int length = (int) (Math.pow(2.0, d));
    String[] solutions = new String[length];
    for(int i = 0; i < length; i++) {
      String wert = dynFormula.get("" + i + "");
      solutions[i] = "" + wert.charAt(wert.length() - 1);
    }
    boolean correct = bft.compareStringArray(solutions);
    String[] answer = bft.getWahrheitsVectorString();

    return ok(boolsolution.render(UserManagement.getCurrentUser(), new Boolean(correct), bft.getVariablenTabelle(),
        length, solutions, answer, "1", bft));
  }

  public Result newFilloutQuestion() {
    FilloutQuestion question = FilloutQuestion.generateNew();
    return ok(boolquestion.render(UserManagement.getCurrentUser(), question));
  }

}
