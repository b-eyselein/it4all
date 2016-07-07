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
    String varString = dynFormula.get("vars");
    String zvectorString = dynFormula.get("zvector");
    String[] vars = varString.split(",");
    String[] zvector = zvectorString.split(",");
    
    int zeilen = (int) Math.pow(2.0, vars.length);
    int spalten = vars.length;
    
    String[][] tabelle = getTabelle(vars.length);
    boolean[][] wahrheitstafel = new boolean[spalten+1][zeilen];
    for (int i = 0; i<zeilen; i++) {
      for (int j = 0; j<spalten; j++) {
        if (tabelle[j][i].equals("1")) {
          wahrheitstafel[j][i] = true;
        }
      }
      if (zvector[i].equals("1")) {
        wahrheitstafel[spalten][i] = true;
      }
    }
    String dnf = kanonischeDisjunktiveNormalform(vars,wahrheitstafel);
    String knf = kanonischeKonjunktiveNormalform(vars,wahrheitstafel);
    
    return ok(bool_formel_erstellen_s.render(UserManagement.getCurrentUser(), vars, zvector, tabelle, spalten, zeilen, dnf, knf));
  }

  public Result index() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    String learnerSolution = dynFormula.get("learnerSolution");
    String varString = dynFormula.get("vars");
    String zvectorString = dynFormula.get("zvector");
    String[] vars;
    String exception_msg = "";
    boolean correct = false;
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
    }
    int zeilen = (int) Math.pow(2.0, vars.length);
    int spalten = vars.length;
    String formel = null;
    String[] formelvector = null;
    if (learnerSolution != null) {
      try {
        BoolescheFunktionTree bft = BoolescheFunktionParser.parse(learnerSolution, vars);
        correct = bft.compareStringArray(zvector);
        formelvector = bft.getWahrheitsVectorString();
        formel = bft.toString();
      } catch (IllegalArgumentException iae) {
        exception_msg = iae.getMessage();
      }
    }
    return ok(bool_formel_erstellen_q.render(UserManagement.getCurrentUser(), vars, zvector,
        getTabelle(vars.length), spalten, zeilen, learnerSolution, correct, exception_msg, formel, formelvector));
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
  
  public String[][] getTabelle(int anzvars) {
    String[][] vtafel = new String[anzvars][(int) Math.pow(2.0, anzvars)];
    char[] zeile = new char[anzvars];
    for(int i = 0; i < zeile.length; i++) {
      zeile[i] = '0';
    }
    for(int i = 0; i < Math.pow(2, anzvars); i++) {
      for(int j = 0; j < zeile.length; j++) {
        vtafel[j][i] = ""+zeile[j];
      }
      int k = anzvars - 1;
      if(zeile[anzvars - 1] == '1') {
        while(k > 0 && zeile[k] == '1') {
          zeile[k] = '0';
          k--;
        }
        zeile[k] = '1';
      } else {
        zeile[anzvars - 1] = '1';
      }
    }
    return vtafel;
  }
  
  public String kanonischeDisjunktiveNormalform(String[] variablen, boolean[][] wahrheitstafel) {
	    String formel = "";
	    for(int i = 0; i < wahrheitstafel[0].length; i++) {
	      // Nur "true"-Werte in der Tafel
	      if(wahrheitstafel[variablen.length][i]) {
	        if(formel.length() != 0) {
	          formel += " OR ";
	        }
	        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
	          if(j != 0) {
	            formel += " AND ";
	          }
	          if(wahrheitstafel[j][i]) {
	            formel += variablen[j];
	          } else {
	            formel += "NOT " + variablen[j];
	          }
	        }
	      }
	    }
	    return formel;
	  }
	  
	  public String kanonischeKonjunktiveNormalform(String[] variablen, boolean[][] wahrheitstafel) {
	    String formel = "";
	    for(int i = 0; i < wahrheitstafel[0].length; i++) {
	      // Nur "false"-Werte in der Tafel
	      if(!wahrheitstafel[variablen.length][i]) {
	        if(formel.length() != 0) {
	          formel += " AND ";
	        }
	        formel += "(";
	        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
	          if(j != 0) {
	            formel += " OR ";
	          }
	          if(!wahrheitstafel[j][i]) {
	            formel += variablen[j];
	          } else {
	            formel += "NOT " + variablen[j];
	          }
	        }
	        formel += ")";
	      }
	    }
	    return formel;
	  }
  
  
}
