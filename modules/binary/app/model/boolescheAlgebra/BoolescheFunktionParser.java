package model.boolescheAlgebra;

import java.util.Arrays;
import java.util.TreeSet;

import model.boolescheAlgebra.BFTree.*;

public class BoolescheFunktionParser {
  
  // TODO: einzelne Methoden beim Parsen.
  
  /*
   * ----------------------------------------------------------------
   * Gibt die Formel als Tree zurueck, der interpretiert werden kann.
   * ----------------------------------------------------------------
   */
  public static BoolescheFunktionTree getBFTree(String originalformel) throws IllegalArgumentException {
    String formel = originalformel.toLowerCase();
    
    // Pruefung auf fehlende Klammern
    pruefeKlammern(originalformel);
    
    // Pruefung auf ungueltige Zeichen
    TreeSet<Character> zeichensatz = new TreeSet<Character>();
    zeichensatz.addAll(Arrays.asList('0', '1', '(', ')', ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
    for(int i = 0; i < formel.length(); i++) {
      if(!zeichensatz.contains(formel.charAt(i))) {
        String iaexception = "illegal caracter \"" + formel.charAt(i) + "\" at:\n" + originalformel + "\n";
        for(int j = 0; j < i; j++) {
          iaexception += " ";
        }
        iaexception += "^";
        throw new IllegalArgumentException(iaexception);
      }
    }
    // Extrahierung der Variablen
    String variablen = formel.replaceAll("and", " ");
    variablen = variablen.replaceAll("xor", " ");
    variablen = variablen.replaceAll("or", " ");
    variablen = variablen.replaceAll("not", " ");
    variablen = variablen.replaceAll("0", " ");
    variablen = variablen.replaceAll("1", " ");
    variablen = variablen.replace('(', ' ');
    variablen = variablen.replace(')', ' ');
    String[] splitf = variablen.split(" ");
    TreeSet<String> vars = new TreeSet<String>();
    for(int i = 0; i < splitf.length; i++) {
      if(!splitf[i].equals("")) {
        vars.add(splitf[i]);
      }
    }
    // Fehler falls keine Variablen vorhanden
    if(vars.size() == 0) {
      throw new IllegalArgumentException("no variables found: " + originalformel);
    }
    BF_Variable[] bf_vars = new BF_Variable[vars.size()];
    int i_vars = 0;
    for(String s: vars) {
      bf_vars[i_vars] = new BF_Variable(s);
      i_vars++;
    }
    return new BoolescheFunktionTree(getNextKnoten(formel, bf_vars), bf_vars);
  }
  
  /*
   * ---------------------------´
   * parst Teilstueck der Formel
   * ---------------------------
   */
  private static BFKnoten getNextKnoten(String ausdruck, BF_Variable[] vars) throws IllegalArgumentException {
    // entfernt fuehrende und anhaengend Leerzeichen und Klammern
    ausdruck = ausdruck.trim();
    while(ausdruck.startsWith("(") && ausdruck.endsWith(")")) {
      ausdruck = ausdruck.substring(1, ausdruck.length() - 1).trim();
    }
    // suche xor
    int klammer = 0;
    for(int i = 0; i < ausdruck.length() - 2; i++) {
      if(ausdruck.charAt(i) == '(') {
        klammer++;
      } else if(ausdruck.charAt(i) == ')') {
        klammer--;
      }
      if(klammer == 0) {
        if(ausdruck.substring(i, i + 3).equals("xor")) {
          return new BF_XOR(getNextKnoten(ausdruck.substring(0, i), vars),
              getNextKnoten(ausdruck.substring(i + 3, ausdruck.length()), vars));
        }
      }
    }
    // suche or
    klammer = 0;
    for(int i = 0; i < ausdruck.length() - 1; i++) {
      if(ausdruck.charAt(i) == '(') {
        klammer++;
      } else if(ausdruck.charAt(i) == ')') {
        klammer--;
      }
      if(klammer == 0) {
        if(ausdruck.substring(i, i + 2).equals("or")) {
          return new BF_OR(getNextKnoten(ausdruck.substring(0, i), vars),
              getNextKnoten(ausdruck.substring(i + 2, ausdruck.length()), vars));
        }
      }
    }
    // suche and
    klammer = 0;
    for(int i = 0; i < ausdruck.length() - 2; i++) {
      if(ausdruck.charAt(i) == '(') {
        klammer++;
      } else if(ausdruck.charAt(i) == ')') {
        klammer--;
      }
      if(klammer == 0) {
        if(ausdruck.substring(i, i + 3).equals("and")) {
          return new BF_AND(getNextKnoten(ausdruck.substring(0, i), vars),
              getNextKnoten(ausdruck.substring(i + 3, ausdruck.length()), vars));
        }
      }
    }
    // suche not
    if(ausdruck.length() > 3 && ausdruck.substring(0, 3).equals("not")) {
      String next = ausdruck.substring(3, ausdruck.length());
      if(next.equals("")) {
        throw new IllegalArgumentException("NOT-Ausdruck unfollstaendig: " + ausdruck);
      }
      return new BF_NOT(getNextKnoten(next, vars));
    }
    
    // Variablen
    for(BF_Variable var: vars) {
      if(ausdruck.equals(var.toString())) {
        return var;
      }
    }
    // Konstanten
    if(ausdruck.equals("0")) {
      return new BF_0();
    }
    if(ausdruck.equals("1")) {
      return new BF_1();
    }
    
    throw new IllegalArgumentException("fehlende Variable / fehlender Ausdruck"); // TODO: Fehlerbeschreibung ergaenzen
  }
  
  private static void pruefeKlammern(String formel) throws IllegalArgumentException {
 // Pruefung auf fehlende Klammern
    int klammerauf = 0;
    for(int i = 0; i < formel.length(); i++) {
      if(formel.charAt(i) == '(') {
        klammerauf++;
      } else if(formel.charAt(i) == ')') {
        klammerauf--;
        if(klammerauf < 0) {
          String iaexception = "opening bracket is missing:\n" + formel + "\n";
          for(int j = 0; j < i; j++) {
            iaexception += " ";
          }
          iaexception += "^";
          throw new IllegalArgumentException(iaexception);
        }
      }
    }
    if(klammerauf > 0) {
      String iaexception = "closing bracket is missing:\n" + formel + "\n";
      for(int j = 0; j < formel.length() - 1; j++) {
        iaexception += " ";
      }
      iaexception += "^";
      throw new IllegalArgumentException(iaexception);
    }
  }
  
}
