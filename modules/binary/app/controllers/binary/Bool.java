package controllers.binary;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import play.data.DynamicForm;
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

  // FIXME: Übergabe an Client!
  BoolescheFunktionTree bft;
  String[] solutions;
  int length;

  public Result index() {
    bft = BoolescheFunktionenGenerator.neueBoolescheFunktion();
    double d = bft.getAnzahlVariablen();
    length = (int) (Math.pow(2.0, d));
    return ok(
        boolquestion.render(bft.toString(), UserManagement.getCurrentUser(), bft.getVariablenTabelle(), length, bft));
  }

  public Result indexSolution() {
    boolean correct = bft.compareStringArray(solutions);
    // FIXME: initialize with right value...
    String[] answer = new String[solutions.length];
    char[] ansOld = bft.getWahrheitsVectorChar();
    for(int i = 0; i < answer.length; i++) {
      answer[i] = "" + ansOld[i];
    }
    return ok(boolsolution.render(UserManagement.getCurrentUser(), new Boolean(correct), bft.toString(),
        bft.getVariablenTabelle(), length, solutions, answer, "1", bft));
  }

  public Result tableAdd() {
    solutions = new String[length];
    DynamicForm dynFormula = factory.form().bindFromRequest();
    for(int i = 0; i < length; i++) {
      solutions[i] = dynFormula.get("" + i + "");
    }
    return redirect(routes.Bool.indexSolution());
  }
}
