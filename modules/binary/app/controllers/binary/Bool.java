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

  public Result indexSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    char[] variables = parseVariables(dynFormula.get("variablen"));

    BoolescheFunktionTree bft = BoolescheFunktionParser.parse(dynFormula.get("bft"), variables);
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

  private char[] parseVariables(String varString) {
    if(varString.endsWith(","))
      varString = varString.substring(0, varString.length() - 1);

    String[] variablesAsString = varString.split(", ");
    char[] variables = new char[variablesAsString.length];
    int i = 0;
    for(String v: variablesAsString)
      variables[i] = v.charAt(0);

    return variables;
  }
}
