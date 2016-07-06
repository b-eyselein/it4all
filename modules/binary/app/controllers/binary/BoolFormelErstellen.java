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
  
  private final static char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  
  @Inject
  private FormFactory factory;

  public Result musterLoesung() {
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
    DynamicForm dynFormula = factory.form().bindFromRequest();
    String learnerSolution = dynFormula.get("learnerSolution");
    String varString = dynFormula.get("vars");
    String zvectorString = dynFormula.get("zvector");
    String[] vars;
    if (varString == null) {
      vars = getRandomVars(2,3);
    } else {
      vars = varString.split(",");
    }
    String[] zvector = null;
    if (zvectorString == null) {
      zvector = getRandomVector(vars.length);
    } else {
      zvector = zvectorString.split(",");
      //throw new IllegalStateException(vars.length+" "+zvector.length); // TODO: remove
    }
    
    int zeilen = (int) Math.pow(2.0, vars.length);
    int spalten = vars.length;
    return ok(bool_formel_erstellen_q.render(UserManagement.getCurrentUser(), vars, zvector,
        getTabelle(vars.length), spalten, zeilen, learnerSolution, true));
  }
  
  private String[] getRandomVector(int vars) {
    String[] vector = new String[(int) Math.pow(2.0, vars)];
    for (int i = 0; i<vector.length; i++) {
      vector[i] = "" +(int) Math.floor(Math.random()*2);
    }
    return vector;
  }

  private String[] getRandomVars(int min, int max) {
    if (max < 1) {
      throw new IllegalArgumentException("max have to be greater or equal 1, but it was " +max);
    }
    if (min < 1) {
      throw new IllegalArgumentException("min have to be greater or equal 1, but it was " +min);
    }
    if (max < min) {
      throw new IllegalArgumentException("min have to be smaller or equal max, min=" +min +"; max=" +max +";");
    }
    if (max > ALPHABET.length) {
      throw new IllegalArgumentException("the ALPHABET does not have enough letters to cover an amount of " +max +" variables");
    }
    int anzVars = (int) Math.floor(min+Math.random()*(max-min+1));
    String[] vars = new String[anzVars];
    for (int i = 0; i<anzVars; i++) {
      vars[i] = ""+ALPHABET[i];
    }
    return vars;
  }
  
  public String[][] getTabelle(int vars) {
    String[][] vtafel = new String[vars][(int) Math.pow(2.0, vars)];
    char[] zeile = new char[vars];
    for(int i = 0; i < zeile.length; i++) {
      zeile[i] = '0';
    }
    for(int i = 0; i < Math.pow(2, vars); i++) {
      for(int j = 0; j < zeile.length; j++) {
        vtafel[j][i] = ""+zeile[j];
      }
      int k = vars - 1;
      if(zeile[vars - 1] == '1') {
        while(k > 0 && zeile[k] == '1') {
          zeile[k] = '0';
          k--;
        }
        zeile[k] = '1';
      } else {
        zeile[vars - 1] = '1';
      }
    }
    return vtafel;
  }
  
  /*
  private String neueAufgabe() {
    String aufgabe = "";
    int anzVars = (int) Math.floor(2+Math.random()*2);
    for (int i = 0; i<anzVars; i++) {
      aufgabe += ALPHABET[i]+",";
    }
    aufgabe += ";";
    int vsize = (int) Math.pow(2, anzVars);
    for (int i = 0; i<vsize; i++) {
      aufgabe += (int) Math.floor(Math.random()*2) +",";
    }
    return aufgabe;
  }*/
}
