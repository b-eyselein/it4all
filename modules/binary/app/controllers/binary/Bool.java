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
    return ok(boolquestion.render(parseFormel(bft.toString()), UserManagement.getCurrentUser(), bft.getVariablenTabelle(), length, bft));
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
    return ok(boolsolution.render(UserManagement.getCurrentUser(), correct, parseFormel(bft.toString()), bft.getVariablenTabelle(), length, solutions, answer, exception_msg, bft));
  }

  public Result tableAdd() {
    solutions = new String[length];
    DynamicForm dynFormula = factory.form().bindFromRequest();
    for(int i = 0; i < length; i++) {
      solutions[i] = dynFormula.get("" + i + "");
    }
    return redirect(routes.Bool.indexSolution());
  }
  
  private String parseFormel(String formel) {
    String s = "";
    int i = 0;
    while (i<formel.length()) {
      if (i+2<formel.length() && formel.substring(i, i+3).equals("XOR")) {
        s += "\u2295";
        i += 3;
      } else if (i+2<formel.length() && formel.substring(i, i+3).equals("NOT")) {
        s += "\u00ac";
        i += 3;
      } else if (i+2<formel.length() && formel.substring(i, i+3).equals("AND")) {
        s += "\u2227";
        i += 3;
      } else if (i+1<formel.length() && formel.substring(i, i+2).equals("OR")) {
        s += "\u2228";
        i += 2;
      } else {
        s += formel.charAt(i);
        i++;
      }
    }
    return s;
  }
}
