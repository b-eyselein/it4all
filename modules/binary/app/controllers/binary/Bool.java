package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.boolquestion;
import views.html.boolsolution;
import model.boolescheAlgebra.*;
import model.boolescheAlgebra.BFTree.*;

@Security.Authenticated(Secured.class)
public class Bool extends Controller {

  @Inject
  private FormFactory factory;
  
  public Result index() {
    BoolescheFunktionTree bft = BoolescheFunktionenGenerator.neueBoolescheFunktion();
    double d = bft.getAnzahlVariablen();
    int length = (int) (Math.pow(2.0, d));
    return ok(
        boolquestion.render(UserManagement.getCurrentUser(), bft.getVariablenTabelle(), length, bft));
  }

  public Result indexSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    BoolescheFunktionTree bft = BoolescheFunktionParser.parse(dynFormula.get("bft"));
    double d = bft.getAnzahlVariablen();
    int length = (int) (Math.pow(2.0, d));
    String[] solutions = new String[length];
    for(int i = 0; i < length; i++) {
      solutions[i] = dynFormula.get("" + i + "");
    }
    boolean correct = bft.compareStringArray(solutions);
    String[] answer = new String[length];
    char[] ansOld = bft.getWahrheitsVectorChar();
    for(int i = 0; i < answer.length; i++) {
      answer[i] = "" + ansOld[i];
    }
    return ok(boolsolution.render(UserManagement.getCurrentUser(), new Boolean(correct),
        bft.getVariablenTabelle(), length, solutions, answer, "1", bft));
  }
}
