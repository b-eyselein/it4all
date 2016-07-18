package model.boolescheAlgebra;

import java.util.Arrays;
import java.util.TreeSet;

import model.boolescheAlgebra.BFTree.BinaryOperator.And;
import model.boolescheAlgebra.BFTree.BinaryOperator.Equivalent;
import model.boolescheAlgebra.BFTree.BinaryOperator.Implication;
import model.boolescheAlgebra.BFTree.BinaryOperator.NAnd;
import model.boolescheAlgebra.BFTree.BinaryOperator.NOr;
import model.boolescheAlgebra.BFTree.BinaryOperator.Or;
import model.boolescheAlgebra.BFTree.BinaryOperator.Xor;
import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;
import model.boolescheAlgebra.BFTree.False;
import model.boolescheAlgebra.BFTree.Node;
import model.boolescheAlgebra.BFTree.NodeType;
import model.boolescheAlgebra.BFTree.Not;
import model.boolescheAlgebra.BFTree.True;
import model.boolescheAlgebra.BFTree.Variable;

public class BoolescheFunktionParser {
  
  private static final TreeSet<Character> zeichensatz = new TreeSet<>(
      Arrays.asList('0', '1', '(', ')', ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
          'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
  
  /**
   * Gibt die Formel als Tree zurueck, der interpretiert werden kann.
   */
  public static BoolescheFunktionTree parse(String originalformel) throws IllegalArgumentException {
    String formel = originalformel.toLowerCase();
    
    substituteGermanOperators(formel);
    
    // Pruefung auf fehlende Klammern
    pruefeKlammern(formel);
    
    // Pruefung auf ungueltige Zeichen
    pruefeZeichensatz(formel);
    
    // Extrahierung der Variablen
    String[] extractedVariables = extractVariables(formel);
    
    TreeSet<String> vars = new TreeSet<>();
    for(int i = 0; i < extractedVariables.length; i++) {
      if(!extractedVariables[i].equals("")) {
        vars.add(extractedVariables[i]);
      }
    }
    // Fehler falls keine Variablen vorhanden
    if(vars.size() == 0) {
      throw new IllegalArgumentException("Die Formel enth\u00e4lt keine Variablen: " + originalformel);
    }
    Variable[] bf_vars = new Variable[vars.size()];
    int i_vars = 0;
    for(String s: vars) {
      bf_vars[i_vars] = new Variable(s.charAt(0));
      i_vars++;
    }
    return new BoolescheFunktionTree(getNextKnoten(formel, bf_vars), bf_vars);
  }
  
  /**
   * Wie parse(formel) nur mit uebergabe der Variablenliste. Gibt die Formel als
   * Tree zurueck, der interpretiert werden kann.
   */
  public static BoolescheFunktionTree parse(String originalformel, char[] variablen) throws IllegalArgumentException {
    String formel = originalformel.toLowerCase();
    
    substituteGermanOperators(formel);
    
    // Pruefung auf fehlende Klammern
    pruefeKlammern(originalformel);
    
    // Pruefung auf ungueltige Zeichen
    pruefeZeichensatz(formel);
    
    String[] extractedVariables = extractVariables(formel);
    
    // Pruefung der Variablen
    TreeSet<Character> vars = new TreeSet<>();
    for(int i = 0; i < variablen.length; i++) {
      vars.add(variablen[i]);
    }
    for(String s: extractedVariables) {
      if(!s.equals("") && !vars.contains(s)) {
        throw new IllegalArgumentException("Die Formel enth\u00e4lt eine unbekannte Variable: " + s);
      }
    }
    // Fehler falls keine Variablen vorhanden
    if(vars.size() == 0) {
      throw new IllegalArgumentException("Die Formel enth\u00e4lt keine Variablen: " + originalformel);
    }
    Variable[] bf_vars = new Variable[vars.size()];
    int i_vars = 0;
    for(char s: vars) {
      bf_vars[i_vars] = new Variable(s);
      i_vars++;
    }
    return new BoolescheFunktionTree(getNextKnoten(formel, bf_vars), bf_vars);
  }
  
  public static Node parseNew(String formula) {
    formula = formula.toLowerCase();
    substituteGermanOperators(formula);
    
    // remove outer parantheses like in (a or b)
    formula = trimAndRemoveParantheses(formula);
    
    int parenthesisDepth = 0;
    String read = "";
    
    int highestOperatorPosition = -1;
    String highestOperator = null;
    
    for(int i = 0; i < formula.length(); i++) {
      char readChar = formula.charAt(i);
      switch(readChar) {
      case '(':
        parenthesisDepth++;
        break;
      case ')':
        parenthesisDepth--;
        break;
      case ' ':
        if(read.length() > 1 && parenthesisDepth == 0) {
          // higheset operator found, ignore everything else
          highestOperator = read;
          highestOperatorPosition = i - highestOperator.length() - 1;
        }
        read = "";
        break;
      default:
        read += readChar;
        break;
      }
    }
    
    if(highestOperator == null) {
      // Kein Operator ==> Variable!
      if(formula.length() > 1)
        throw new IllegalArgumentException("Fehler: >>" + formula + "<<");
      
      if(formula.equals("1"))
        return new True();
      else if(formula.equals("0"))
        return new False();
      else
        return new Variable(formula.charAt(0));
    }
    
    String rightFormula = formula.substring(highestOperatorPosition + highestOperator.length() + 2);
    if(highestOperator.equals("not")) {
      return NodeType.NOT.instantiate(parseNew(rightFormula));
    } else {
      String leftFormula = formula.substring(0, highestOperatorPosition);
      NodeType type = NodeType.get(highestOperator);
      
      if(type == null)
        throw new IllegalArgumentException("No operator is defined for: " + highestOperator);
      
      Node left = parseNew(leftFormula);
      Node right = parseNew(rightFormula);
      
      return type.instantiate(left, right);
    }
  }
  
  /**
   * Liefert alle Variablen, die in der Formel enthalten sind. Kann leere
   * Strings enthalten.
   *
   * @param String
   *          formel
   * @return String[] variablen
   */
  private static String[] extractVariables(String formel) {
    String variablen = formel
        //@formatter:off
        .replaceAll("nand", " ")
        .replaceAll("nor", " ")
        .replaceAll("equiv", " ")
        .replaceAll("impl", " ")
        .replaceAll("and", " ")
        .replaceAll("xor", " ")
        .replaceAll("or", " ")
        .replaceAll("not", " ")
        .replaceAll("0", " ")
        .replaceAll("1", " ")
        .replaceAll("\\(", " ")
        .replaceAll("\\)", " ");
        // @formatter:on
    return variablen.split(" ");
  }
  
  /**
   * parst Teilstueck der Formel
   */
  private static Node getNextKnoten(String ausdruck, Variable[] vars) throws IllegalArgumentException {
    // entfernt fuehrende und anhaengend Leerzeichen und Klammern
    ausdruck = ausdruck.trim();
    int klammer = 0;
    boolean von_klammer_umschlossen = true;
    while(ausdruck.startsWith("(") && ausdruck.endsWith(")") && von_klammer_umschlossen) {
      for(int i = 0; i < ausdruck.length(); i++) {
        if(ausdruck.charAt(i) == '(') {
          klammer++;
        } else if(ausdruck.charAt(i) == ')') {
          klammer--;
          if(klammer == 0 && i != ausdruck.length() - 1) {
            von_klammer_umschlossen = false;
          }
        }
      }
      if(von_klammer_umschlossen) {
        ausdruck = ausdruck.substring(1, ausdruck.length() - 1).trim();
      }
    }
    // suche xor
    klammer = 0;
    for(int i = ausdruck.length() - 1; i >= 0; i--) {
      if(ausdruck.charAt(i) == ')') {
        klammer++;
      } else if(ausdruck.charAt(i) == '(') {
        klammer--;
      }
      if(klammer == 0) {
        try {
          if(ausdruck.substring(i, i + 3).equals("xor")) {
            return new Xor(getNextKnoten(ausdruck.substring(0, i), vars),
                getNextKnoten(ausdruck.substring(i + 3, ausdruck.length()), vars));
          } else if(ausdruck.substring(i, i + 3).equals("nor")) {
            return new NOr(getNextKnoten(ausdruck.substring(0, i), vars),
                getNextKnoten(ausdruck.substring(i + 3, ausdruck.length()), vars));
          } else if(ausdruck.substring(i, i + 4).equals("nand")) {
            return new NAnd(getNextKnoten(ausdruck.substring(0, i), vars),
                getNextKnoten(ausdruck.substring(i + 4, ausdruck.length()), vars));
          } else if(ausdruck.substring(i, i + 5).equals("equiv")) {
            return new Equivalent(getNextKnoten(ausdruck.substring(0, i), vars),
                getNextKnoten(ausdruck.substring(i + 5, ausdruck.length()), vars));
          }
        } catch (IndexOutOfBoundsException e) {
          
        }
      }
    }
    // suche implication
    klammer = 0;
    for(int i = 0; i < ausdruck.length() - 3; i++) {
      if(ausdruck.charAt(i) == '(') {
        klammer++;
      } else if(ausdruck.charAt(i) == ')') {
        klammer--;
      }
      if(klammer == 0) {
        if(ausdruck.substring(i, i + 4).equals("impl")) {
          return new Implication(getNextKnoten(ausdruck.substring(0, i), vars),
              getNextKnoten(ausdruck.substring(i + 4, ausdruck.length()), vars));
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
          return new Or(getNextKnoten(ausdruck.substring(0, i), vars),
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
          return new And(getNextKnoten(ausdruck.substring(0, i), vars),
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
      return new Not(getNextKnoten(next, vars));
    }
    
    // Variablen
    for(Variable var: vars) {
      if(ausdruck.equals(var.toString())) {
        return var;
      }
    }
    // Konstanten
    if(ausdruck.equals("0")) {
      return new False();
    }
    if(ausdruck.equals("1")) {
      return new True();
    }
    
    if(ausdruck.equals("")) {
      throw new IllegalArgumentException("Der Ausdruck ist unvollst\u00e4ndig. Es fehlt eine Variable.");
    }
    
    int zu_viele_vars = 0;
    for(Variable var: vars) {
      if(ausdruck.contains(var.toString())) {
        zu_viele_vars++;
      }
    }
    if(zu_viele_vars > 1) {
      throw new IllegalArgumentException("Zwischen den Variablen \"" + ausdruck + "\" fehlt ein Operator.");
    }
    
    throw new IllegalArgumentException(
        "Der Ausdruck ist unvollst\u00e4ndig. Es fehlt ein Operator oder eine Variable.");
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
        String iaexception = "Die Formel enth\u00e4lt ein ung\u00fcltiges Zeichen: " + formel.charAt(i);
        throw new IllegalArgumentException(iaexception);
      }
    }
    return true;
  }
  
  /**
   * Substituiert alle deutschen Operatoren durch aequivalente englische
   * Operatoren.
   *
   * @return formel
   */
  private static void substituteGermanOperators(String formel) {
    for(NodeType type: NodeType.values())
      formel = formel.replaceAll(type.getGermanOperator(), type.getEnglishOperator());
  }
  
  private static String trimAndRemoveParantheses(String formula) {
    formula = formula.trim();
    
    if(!formula.startsWith("(") && !formula.endsWith(")"))
      return formula;
    
    int counter = 1;
    // Ignore but count first paranthesis
    for(int i = 1; i < formula.length(); i++) {
      if(formula.charAt(i) == '(')
        counter++;
      else if(formula.charAt(i) == ')')
        counter--;
      
      if(counter == 0) {
        // Found matching bracket
        if(i == formula.length() - 1)
          // Matching parantheses on first and last position, remove
          formula = formula.substring(1, formula.length() - 1);
        else {
          // Matching ps not on first and last position, to be removed in
          // later call
          return formula;
        }
      }
    }
    return formula;
  }
  
}
