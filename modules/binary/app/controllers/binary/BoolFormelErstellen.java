package controllers.binary;

import java.util.TreeSet;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.boolescheAlgebra.CreationQuestion;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.bool_formel_erstellen_q;
import views.html.bool_formel_erstellen_s;

@Security.Authenticated(Secured.class)
public class BoolFormelErstellen extends Controller {

  private static String kurzeDisjunktiveNormalform(String[] vars, boolean[][] wahrheitstafel) {
    String formel = "";
    // suche wahre Eintraege in der Wahrheitstafel
    TreeSet<String> neueAusdruecke = new TreeSet<>();
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
    TreeSet<String> kuerzereAusdruecke = new TreeSet<>();
    TreeSet<String> benutzteAusdruecke = new TreeSet<>();
    do {
      kuerzereAusdruecke = new TreeSet<>();
      benutzteAusdruecke = new TreeSet<>();
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
    TreeSet<String> neueAusdruecke = new TreeSet<>();
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
    TreeSet<String> kuerzereAusdruecke = new TreeSet<>();
    TreeSet<String> benutzteAusdruecke = new TreeSet<>();
    do {
      kuerzereAusdruecke = new TreeSet<>();
      benutzteAusdruecke = new TreeSet<>();
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

  @Inject
  private FormFactory factory;

  public Result checkSolution() {
    return ok("TODO!");
  }

  public Result index() {
    CreationQuestion question = CreationQuestion.generateNew();
    return ok(bool_formel_erstellen_q.render(UserManagement.getCurrentUser(), question));
  }

  public Result musterLoesung() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    String varString = dynFormula.get("vars");
    String zvectorString = dynFormula.get("zvector");
    String[] vars = varString.split(",");
    String[] zvector = zvectorString.split(",");
    
    int zeilen = (int) Math.pow(2.0, vars.length);
    int spalten = vars.length;
    
    // CreationQuestion question = new CreationQuestion(theVariables,
    // theSolutions);
    // List<Assignment> solution = question.
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

  private String[][] getTabelle(int anzvars) {
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

  private String kanonischeDisjunktiveNormalform(String[] variablen, boolean[][] wahrheitstafel) {
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

  private String kanonischeKonjunktiveNormalform(String[] variablen, boolean[][] wahrheitstafel) {
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
