package model.boolescheAlgebra;

import java.util.Arrays;
import java.util.TreeSet;

import model.boolescheAlgebra.BFTree.*;

public class BoolescheFunktionParser {
  
  private static final TreeSet<Character> zeichensatz = new TreeSet<Character>(
      Arrays.asList('0', '1', '(', ')', ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
          'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
          
  /**
   * ----------------------------------------------------------------
   * Gibt die Formel als Tree zurueck, der interpretiert werden kann.
   * ----------------------------------------------------------------
   */
  public static BoolescheFunktionTree getBFTree(String originalformel) throws IllegalArgumentException {
    String formel = originalformel.toLowerCase();
    
    formel = substitution(formel);
    
    // Pruefung auf fehlende Klammern
    pruefeKlammern(originalformel);
    
    // Pruefung auf ungueltige Zeichen
    pruefeZeichensatz(formel);
    
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
      throw new IllegalArgumentException("Die Formel enth\u00e4lt keine Variablen: " + originalformel);
    }
    BF_Variable[] bf_vars = new BF_Variable[vars.size()];
    int i_vars = 0;
    for(String s: vars) {
      bf_vars[i_vars] = new BF_Variable(s);
      i_vars++;
    }
    return new BoolescheFunktionTree(getNextKnoten(formel, bf_vars), bf_vars);
  }
  
  /**
   * ----------------------------------------------------------------
   * Wie getBFTree(formel) nur mit uebergabe der Variablenliste.
   * Gibt die Formel als Tree zurueck, der interpretiert werden kann.
   * ----------------------------------------------------------------
   */
  public static BoolescheFunktionTree getBFTreeMitVars(String originalformel, String[] variablen)
      throws IllegalArgumentException {
    String formel = originalformel.toLowerCase();
    
    formel = substitution(formel);
    
    // Pruefung auf fehlende Klammern
    pruefeKlammern(originalformel);
    
    // Pruefung auf ungueltige Zeichen
    pruefeZeichensatz(formel);
    
    // Extrahierung der Variablen
    String f_variablen = formel.replaceAll("and", " ");
    f_variablen = f_variablen.replaceAll("xor", " ");
    f_variablen = f_variablen.replaceAll("or", " ");
    f_variablen = f_variablen.replaceAll("not", " ");
    f_variablen = f_variablen.replaceAll("0", " ");
    f_variablen = f_variablen.replaceAll("1", " ");
    f_variablen = f_variablen.replace('(', ' ');
    f_variablen = f_variablen.replace(')', ' ');
    String[] splitf = f_variablen.split(" ");
    
    // Pruefung der Variablen
    TreeSet<String> vars = new TreeSet<String>();
    for(int i = 0; i < variablen.length; i++) {
      vars.add(variablen[i]);
    }
    for(String s: splitf) {
      if(!s.equals("") && !vars.contains(s)) {
        throw new IllegalArgumentException("Die Formel enth\u00e4lt eine unbekannte Variable: " + s);
      }
    }
    // Fehler falls keine Variablen vorhanden
    if(vars.size() == 0) {
      throw new IllegalArgumentException("Die Formel enth\u00e4lt keine Variablen: " + originalformel);
    }
    BF_Variable[] bf_vars = new BF_Variable[vars.size()];
    int i_vars = 0;
    for(String s: vars) {
      bf_vars[i_vars] = new BF_Variable(s);
      i_vars++;
    }
    return new BoolescheFunktionTree(getNextKnoten(formel, bf_vars), bf_vars);
  }

  /**
   * ---------------------------´ parst Teilstueck der Formel
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
        throw new IllegalArgumentException("NOT-Ausdruck unfollst\u00e4ndig: " + ausdruck);
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
    
    throw new IllegalArgumentException(
        "Der Ausdruck ist unvollst\u00e4ndig. M\u00f6glicherweise fehlt bei einem Operator eine Variable."); // TODO:
                                                                                                             // Fehlerbeschreibung
                                                                                                             // ergaenzen
  }
  
  /**
   * Pruefung auf fehlende Klammern
   */
  private static boolean pruefeKlammern(String formel) throws IllegalArgumentException {
    int klammerauf = 0;
    for(int i = 0; i < formel.length(); i++) {
      if(formel.charAt(i) == '(') {
        klammerauf++;
      } else if(formel.charAt(i) == ')') {
        klammerauf--;
        if(klammerauf < 0) {
          String iaexception = "Bei der Formel fehlt eine \u00f6ffnende Klammer: " + formel;
          throw new IllegalArgumentException(iaexception);
        }
      }
    }
    if(klammerauf > 0) {
      String iaexception = "Bei der Formel fehlt eine schlie\u00dfende Klammer: " + formel;
      throw new IllegalArgumentException(iaexception);
    }
    return true;
  }
  
  /**
   * Pruefung auf ungueltige Zeichen
   */
  private static boolean pruefeZeichensatz(String formel) throws IllegalArgumentException {
    for(int i = 0; i < formel.length(); i++) {
      if(!zeichensatz.contains(formel.charAt(i))) {
        String iaexception = "Deine L\u00f6sung enth\u00e4lt ein ung\u00fcltiges Zeichen: " + formel.charAt(i);
        throw new IllegalArgumentException(iaexception);
      }
    }
    return true;
  }
  
  private static String substitution(String formel) {
    formel = formel.replaceAll("xoder", "xor")
                   .replaceAll("oder", "or")
                   .replaceAll("und", "and")
                   .replaceAll("nicht", "not");
    return formel;
  }
  
}
