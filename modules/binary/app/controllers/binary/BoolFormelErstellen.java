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
  private BoolescheFunktionTree bft;
  private String learnerSolution;
  private int zeilen;
  private int spalten;
  
  public Result addLearnerSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    learnerSolution = dynFormula.get("learnerSolution");
    return redirect(routes.BoolFormelErstellen.checkSolution());
  }
  
  public Result checkSolution() {
    String exception_msg = "";
    boolean correct = false;
    try {
      correct = bft.compareBoolscheFormelTree(BoolescheFunktionParser.getBFTreeMitVars(learnerSolution,bft.getVariablen()));
    } catch (IllegalArgumentException e) {
      exception_msg = e.getMessage();
    }
    return ok(bool_formel_erstellen_s.render(UserManagement.getCurrentUser(), learnerSolution, correct, exception_msg,
        bft.getVariablen(), bft.getWahrheitstafelChar(), spalten, zeilen));
  }
  
  public Result index() {
    bft = BoolescheFunktionenGenerator.neueBoolescheFunktion(2,3);
    zeilen = (int) Math.pow(2.0, bft.getAnzahlVariablen());
    spalten = bft.getAnzahlVariablen() + 1;
    return ok(bool_formel_erstellen_q.render(UserManagement.getCurrentUser(), bft.getVariablen(),
        bft.getWahrheitstafelChar(), spalten, zeilen));
  }
}
