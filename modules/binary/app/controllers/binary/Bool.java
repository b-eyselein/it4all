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
  
  public Result index() {
    BoolescheFunktionTree bft = BoolescheFunktionenGenerator.neueBoolescheFunktion();
    String formel = bft.toString();
    if (formel.length() > 25) {
      String alternative_formel_1 = bft.kurzeDisjunktiveNormalform();
      String alternative_formel_2 = bft.kurzeKonjunktiveNormalform();
      if (alternative_formel_1.length() < alternative_formel_2.length() && alternative_formel_1.length() < formel.length()) {
        formel = alternative_formel_1;
      } else if (alternative_formel_2.length() < alternative_formel_1.length() && alternative_formel_2.length() < formel.length()) {
        formel = alternative_formel_2;
      }
    }
    double d = bft.getAnzahlVariablen();
    int length = (int) (Math.pow(2.0, d));
    return ok(
        boolquestion.render(UserManagement.getCurrentUser(), bft.getVariablenTabelle(), length, formel, bft));
  }

  public Result indexSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    String varString = dynFormula.get("variablen");
    if (varString.endsWith(",")) {
      varString = varString.substring(0, varString.length()-1);
    }
    BoolescheFunktionTree bft = BoolescheFunktionParser.parse(dynFormula.get("bft"),varString.split(","));
    double d = bft.getAnzahlVariablen();
    int length = (int) (Math.pow(2.0, d));
    String[] solutions = new String[length];
    for(int i = 0; i < length; i++) {
      String wert = dynFormula.get("" + i + "");
      solutions[i] = ""+wert.charAt(wert.length()-1);
    }
    boolean correct = bft.compareStringArray(solutions);
    String[] answer = bft.getWahrheitsVectorString();
    
    
    return ok(boolsolution.render(UserManagement.getCurrentUser(), new Boolean(correct),
        bft.getVariablenTabelle(), length, solutions, answer, "1", bft));
  }
}
