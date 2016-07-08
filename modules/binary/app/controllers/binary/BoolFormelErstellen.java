package controllers.binary;

import model.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.TreeSet;

import javax.inject.Inject;

import controllers.core.UserManagement;
import views.html.bool_formel_erstellen_q;
import views.html.bool_formel_erstellen_s;
import model.boolescheAlgebra.BoolescheFunktionParser;
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
    boolean[][] wahrheitstafel = new boolean[spalten + 1][zeilen];
    for(int i = 0; i < zeilen; i++) {
      for(int j = 0; j < spalten; j++) {
        if(tabelle[j][i].equals("1")) {
          wahrheitstafel[j][i] = true;
        }
      }
      if(zvector[i].equals("1")) {
        wahrheitstafel[spalten][i] = true;
      }
    }
    String dnf = kanonischeDisjunktiveNormalform(vars, wahrheitstafel);
    String s_dnf = kurzeDisjunktiveNormalform(vars, wahrheitstafel);
    String knf = kanonischeKonjunktiveNormalform(vars, wahrheitstafel);
    String s_knf = kurzeKonjunktiveNormalform(vars, wahrheitstafel);
    
    return ok(bool_formel_erstellen_s.render(UserManagement.getCurrentUser(), vars, zvector, tabelle, spalten, zeilen,
        dnf, s_dnf, knf, s_knf));
  }
  
  private static String kurzeDisjunktiveNormalform(String[] vars, boolean[][] wahrheitstafel) {
    String formel = "";
    // suche wahre Eintraege in der Wahrheitstafel
    TreeSet<String> neueAusdruecke = new TreeSet<String>();
    for(int i = 0; i < wahrheitstafel[0].length; i++) {
      if(wahrheitstafel[wahrheitstafel.length - 1][i]) {
        String neuerAusdruck = "";
        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
          if(wahrheitstafel[j][i]) {
            neuerAusdruck += vars[j];
          } else {
            neuerAusdruck += "NOT " + vars[j];
          }
          if(j < wahrheitstafel.length - 2) {
            neuerAusdruck += ",";
          }
        }
        neueAusdruecke.add(neuerAusdruck);
      }
    }
    // vergleiche alle Ausdruecke miteinander
    TreeSet<String> kuerzereAusdruecke = new TreeSet<String>();
    TreeSet<String> benutzteAusdruecke = new TreeSet<String>();
    do {
      kuerzereAusdruecke = new TreeSet<String>();
      benutzteAusdruecke = new TreeSet<String>();
      for(String ausdruck1: neueAusdruecke) {
        String[] ausdruecke1 = ausdruck1.split(",");
        for(String ausdruck2: neueAusdruecke) {
          String[] ausdruecke2 = ausdruck2.split(",");
          
          int gleich = 0;
          int verschieden = 0;
          String neuerausdruck = "";
          for(int k = 0; k < ausdruecke1.length; k++) {
            if(ausdruecke1[k].equals(ausdruecke2[k])) {
              if(neuerausdruck.equals("")) {
                neuerausdruck += ausdruecke1[k];
              } else {
                neuerausdruck += "," + ausdruecke1[k];
              }
              gleich++;
            } else if(ausdruecke1[k].matches("NOT " + ausdruecke2[k])
                || ausdruecke2[k].matches("NOT " + ausdruecke1[k])) {
              verschieden++;
            }
          }
          if(gleich == ausdruecke1.length - 1 && verschieden == 1) {
            kuerzereAusdruecke.add(neuerausdruck);
            benutzteAusdruecke.add(ausdruck1);
            benutzteAusdruecke.add(ausdruck2);
          }
        }
      }
      for(String ausdruck1: neueAusdruecke) {
        if(!benutzteAusdruecke.contains(ausdruck1)) {
          if(formel.equals("")) {
            formel += ausdruck1;
          } else {
            formel += " OR " + ausdruck1;
          }
        }
      }
      neueAusdruecke = kuerzereAusdruecke;
    } while(!kuerzereAusdruecke.isEmpty());
    formel = formel.replaceAll(",", " AND ");
    return formel;
  }
  
  private static String kurzeKonjunktiveNormalform(String[] vars, boolean[][] wahrheitstafel) {
    String formel = "";
    // suche wahre Eintraege in der Wahrheitstafel
    TreeSet<String> neueAusdruecke = new TreeSet<String>();
    for(int i = 0; i < wahrheitstafel[0].length; i++) {
      if(!wahrheitstafel[wahrheitstafel.length - 1][i]) {
        String neuerAusdruck = "";
        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
          if(!wahrheitstafel[j][i]) {
            neuerAusdruck += vars[j];
          } else {
            neuerAusdruck += "NOT " + vars[j];
          }
          if(j < wahrheitstafel.length - 2) {
            neuerAusdruck += ",";
          }
        }
        neueAusdruecke.add(neuerAusdruck);
      }
    }
    // vergleiche alle Ausdruecke miteinander
    TreeSet<String> kuerzereAusdruecke = new TreeSet<String>();
    TreeSet<String> benutzteAusdruecke = new TreeSet<String>();
    do {
      kuerzereAusdruecke = new TreeSet<String>();
      benutzteAusdruecke = new TreeSet<String>();
      for(String ausdruck1: neueAusdruecke) {
        String[] ausdruecke1 = ausdruck1.split(",");
        for(String ausdruck2: neueAusdruecke) {
          String[] ausdruecke2 = ausdruck2.split(",");
          
          int gleich = 0;
          int verschieden = 0;
          String neuerausdruck = "";
          for(int k = 0; k < ausdruecke1.length; k++) {
            if(ausdruecke1[k].equals(ausdruecke2[k])) {
              if(neuerausdruck.equals("")) {
                neuerausdruck += ausdruecke1[k];
              } else {
                neuerausdruck += "," + ausdruecke1[k];
              }
              gleich++;
            } else if(ausdruecke1[k].matches("NOT " + ausdruecke2[k])
                || ausdruecke2[k].matches("NOT " + ausdruecke1[k])) {
              verschieden++;
            }
          }
          if(gleich == ausdruecke1.length - 1 && verschieden == 1) {
            kuerzereAusdruecke.add(neuerausdruck);
            benutzteAusdruecke.add(ausdruck1);
            benutzteAusdruecke.add(ausdruck2);
          }
        }
      }
      for(String ausdruck1: neueAusdruecke) {
        if(!benutzteAusdruecke.contains(ausdruck1)) {
          if(formel.equals("")) {
            formel += "(" + ausdruck1 + ")";
          } else {
            formel += " AND " + "(" + ausdruck1 + ")";
          }
        }
      }
      neueAusdruecke = kuerzereAusdruecke;
    } while(!kuerzereAusdruecke.isEmpty());
    formel = formel.replaceAll(",", " OR ");
    return formel;
  }
  
  public Result index() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    String learnerSolution = dynFormula.get("learnerSolution");
    String varString = dynFormula.get("vars");
    String zvectorString = dynFormula.get("zvector");
    String[] vars;
    String exception_msg = "";
    boolean correct = false;
    if(varString == null) {
      vars = getRandomVars(2, 3);
    } else {
      vars = varString.split(",");
    }
    String[] zvector = null;
    if(zvectorString == null) {
      zvector = getRandomVector(vars.length);
    } else {
      zvector = zvectorString.split(",");
    }
    int zeilen = (int) Math.pow(2.0, vars.length);
    int spalten = vars.length;
    String formel = null;
    String[] formelvector = null;
    if(learnerSolution != null) {
      try {
        BoolescheFunktionTree bft = BoolescheFunktionParser.parse(learnerSolution, vars);
        correct = bft.compareStringArray(zvector);
        formelvector = bft.getWahrheitsVectorString();
        formel = bft.toString();
      } catch (IllegalArgumentException iae) {
        exception_msg = iae.getMessage();
      }
    }
    return ok(bool_formel_erstellen_q.render(UserManagement.getCurrentUser(), vars, zvector, getTabelle(vars.length),
        spalten, zeilen, learnerSolution, correct, exception_msg, formel, formelvector));
  }
  
  private String[] getRandomVector(int vars) {
    int wert = 0;
    boolean wahrenthalten = false;
    boolean falschenthalten = false;
    String[] vector = new String[(int) Math.pow(2.0, vars)];
    for(int i = 0; i < vector.length; i++) {
      wert = (int) Math.floor(Math.random() * 2);
      if(wert == 1) {
        wahrenthalten = true;
      } else {
        falschenthalten = true;
      }
      vector[i] = "" + wert;
    }
    if(wahrenthalten && falschenthalten) {
      return vector;
    }
    return getRandomVector(vars);
  }
  
  private String[] getRandomVars(int min, int max) {
    if(max < 1) {
      throw new IllegalArgumentException("max have to be greater or equal 1, but it was " + max);
    }
    if(min < 1) {
      throw new IllegalArgumentException("min have to be greater or equal 1, but it was " + min);
    }
    if(max < min) {
      throw new IllegalArgumentException("min have to be smaller or equal max, min=" + min + "; max=" + max + ";");
    }
    if(max > ALPHABET.length) {
      throw new IllegalArgumentException(
          "the ALPHABET does not have enough letters to cover an amount of " + max + " variables");
    }
    int anzVars = (int) Math.floor(min + Math.random() * (max - min + 1));
    String[] vars = new String[anzVars];
    for(int i = 0; i < anzVars; i++) {
      vars[i] = "" + ALPHABET[i];
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
        vtafel[j][i] = "" + zeile[j];
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
