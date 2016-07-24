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
  FormFactory factory;
  BoolescheFunktionTree bft;
  BoolescheFunktionenGenerator bGen = new BoolescheFunktionenGenerator();
  String[] solutions;
  int length;

  public Result index() {
    bft = bGen.neueBoolescheFunktion();
    double d = (double) bft.getAnzahlVariablen();
    length = (int)(Math.pow(2.0, d));
    return ok(boolquestion.render(bft.toString(),bft.getVariablen(), UserManagement.getCurrentUser(), bft.getVariablenTabelle(), length));
  }

  public Result indexSolution() {
    boolean correct = false;
    String exception_msg = "";
    try {
      correct = bft.compareStringArray(solutions);
    } catch (IllegalArgumentException e) {
      exception_msg = e.getMessage();
    }
    String[] answer = new String[solutions.length];
    char[] ansOld = bft.getWahrheitsVectorChar();
    for(int i = 0; i < answer.length; i++){
      answer[i] = ""+ansOld[i];
    }
    return ok(boolsolution.render(UserManagement.getCurrentUser(), correct, bft.toString(), bft.getVariablen(), bft.getVariablenTabelle(), length, solutions, answer, exception_msg));
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
