package controllers.binary;

import model.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

import controllers.core.UserManagement;
import views.html.bool_formel_erstellen_q;
import views.html.bool_formel_erstellen_s;
import model.boolescheAlgebra.BoolescheFunktionParser;
import model.boolescheAlgebra.BoolescheFunktionenGenerator;
import model.boolescheAlgebra.BFTree.*;

@Security.Authenticated(Secured.class)
public class BoolFormelErstellen extends Controller {

  @Inject
  private FormFactory factory;

  public Result checkSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    String learnerSolution = dynFormula.get("learnerSolution");
    String formel = dynFormula.get("boolformel");
    BoolescheFunktionTree bft = BoolescheFunktionParser.parse(formel);
    
    int zeilen = (int) Math.pow(2.0, bft.getAnzahlVariablen());
    int spalten = bft.getAnzahlVariablen() + 1;
    
    String exception_msg = "";
    boolean correct = false;
    try {
      correct = bft.compareBoolscheFormelTree(BoolescheFunktionParser.parse(learnerSolution, bft.getVariablen()));
    } catch (IllegalArgumentException e) {
      exception_msg = e.getMessage();
    }
    return ok(bool_formel_erstellen_s.render(UserManagement.getCurrentUser(), learnerSolution, correct, exception_msg,
        bft.getVariablen(), bft.getWahrheitstafelChar(), spalten, zeilen, bft.kanonischeDisjunktiveNormalform(), bft.kanonischeKonjunktiveNormalform()));
  }

  public Result index() {
    BoolescheFunktionTree bft = BoolescheFunktionenGenerator.neueBoolescheFunktion(2, 3);
    int zeilen = (int) Math.pow(2.0, bft.getAnzahlVariablen());
    int spalten = bft.getAnzahlVariablen() + 1;
    return ok(bool_formel_erstellen_q.render(UserManagement.getCurrentUser(), bft.getVariablen(),
        bft.getWahrheitstafelChar(), spalten, zeilen, bft.toString()));
  }
}
