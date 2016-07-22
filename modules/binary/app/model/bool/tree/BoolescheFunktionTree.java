package model.bool.tree;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import model.bool.BooleanQuestion;
import model.bool.node.Node;

public class BoolescheFunktionTree {
  
  private static char booleantochar(boolean b) {
    return b ? '1' : '0';
  }
  
  private static String booleantoString(boolean b) {
    return booleantochar(b) + "";
  }
  
  private Node rootNode;
  private Character[] variables;
  
  public BoolescheFunktionTree(Node theRootNode) {
    this.rootNode = theRootNode;
    Set<Character> usedVariables = theRootNode.getUsedVariables();
    variables = usedVariables.toArray(new Character[usedVariables.size()]);
  }
  
  public boolean evaluate(Assignment assignment) {
    return rootNode.evaluate(assignment);
  }
  
  /**
   * Gibt den Wahrheitswert der Funktion zu dem uebergebenen bool Array zurueck.
   */
  public boolean evaluate(boolean[] b) throws IllegalArgumentException {
    // TODO: boolean[] to Assignment!
    if(b.length != this.variables.length) {
      throw new IllegalArgumentException("Wrong number of boolean values. Expected " + this.getAnzahlVariablen()
          + " elements but there were " + b.length + " elements.");
    }
    Assignment assignment = new Assignment();
    for(int i = 0; i < this.variables.length; i++)
      assignment.setAssignment(variables[i], b[i]);
    
    return rootNode.evaluate(assignment);
  }
  
  public List<Assignment> evaluateAll(List<Assignment> assignments) {
    for(Assignment assignment: assignments)
      assignment.setAssignment(BooleanQuestion.SOLUTION_VARIABLE, evaluate(assignment));
    return assignments;
  }
  
  /**
   * gibt Anzahl der Variablen zurueck
   */
  public int getAnzahlVariablen() {
    return variables.length;
  }
  
  /**
   * Gibt boolesche Funktion als Sting zurueck.
   */
  public String getAsString() {
    return rootNode.getAsString(false);
  }
  
  /**
   * gibt sortierten String-Array mit Namen der Variablen zurueck
   */
  public Character[] getVariables() {
    return variables;
  }
  
  /**
   * Gibt Wahrheitstafel als bool Array zurueck. boolean[Spalte][Zeile] ;
   * Anzahl_der_Spalten = Anzahl_der_Variablen+1 ; Anzahl_der_Zeilen =
   * 2^Anzahl_der_Variablen ;
   */
  public boolean[][] getWahrheitstafelBoolean() {
    boolean[][] wtafel = new boolean[this.variables.length + 1][(int) Math.pow(2, this.variables.length)];
    boolean[] zeile = new boolean[this.variables.length];
    for(int i = 0; i < Math.pow(2, variables.length); i++) {
      for(int j = 0; j < zeile.length; j++) {
        wtafel[j][i] = zeile[j];
      }
      wtafel[this.variables.length][i] = this.evaluate(zeile);
      int k = variables.length - 1;
      if(zeile[variables.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[variables.length - 1] = true;
      }
    }
    return wtafel;
  }
  
  /**
   * gibt Vector mit den Werten des Ausdrucks zurueck
   */
  public boolean[] getWahrheitsVector() {
    boolean[] wvector = new boolean[(int) Math.pow(2, this.variables.length)];
    boolean[] zeile = new boolean[this.variables.length];
    for(int i = 0; i < Math.pow(2, variables.length); i++) {
      wvector[i] = this.evaluate(zeile);
      int k = variables.length - 1;
      if(zeile[variables.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[variables.length - 1] = true;
      }
    }
    return wvector;
  }
  
  /**
   * gibt Vector mit den Werten als String des Ausdrucks zurueck
   */
  public String[] getWahrheitsVectorString() {
    String[] wvector = new String[(int) Math.pow(2, this.variables.length)];
    boolean[] zeile = new boolean[this.variables.length];
    for(int i = 0; i < Math.pow(2, variables.length); i++) {
      wvector[i] = booleantoString(this.evaluate(zeile));
      int k = variables.length - 1;
      if(zeile[variables.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[variables.length - 1] = true;
      }
    }
    return wvector;
  }
  
  /**
   * Gibt eine aequivalente Formel in kanonischer DNF als String zurueck
   */
  public String kanonischeDisjunktiveNormalform() {
    String formel = "";
    boolean[][] wahrheitstafel = this.getWahrheitstafelBoolean();
    for(int i = 0; i < wahrheitstafel[0].length; i++) {
      // Nur "true"-Werte in der Tafel
      if(wahrheitstafel[this.getAnzahlVariablen()][i]) {
        if(formel.length() != 0) {
          formel += " OR ";
        }
        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
          if(j != 0) {
            formel += " AND ";
          }
          if(wahrheitstafel[j][i]) {
            formel += variables[j];
          } else {
            formel += "NOT " + variables[j];
          }
        }
      }
    }
    return formel;
  }
  
  /**
   * Gibt eine aequivalente Formel in kanonischer KNF als String zurueck
   */
  public String kanonischeKonjunktiveNormalform() {
    String formel = "";
    
    List<Assignment> assignments = evaluateAll(Assignment.generateAllAssignments(variables));
    System.out.println("Generated: " + assignments.size());
    List<Assignment> positives = assignments.stream()
        .filter(assignment -> assignment.getAssignment(BooleanQuestion.SOLUTION_VARIABLE)).collect(Collectors.toList());
    System.out.println("Positives: " + positives.size());
    for(Assignment p: positives)
      System.out.println(p);
    
    boolean[][] wahrheitstafel = this.getWahrheitstafelBoolean();
    for(int i = 0; i < wahrheitstafel[0].length; i++) {
      // Nur "false"-Werte in der Tafel
      if(!wahrheitstafel[this.getAnzahlVariablen()][i]) {
        if(formel.length() != 0) {
          formel += " AND ";
        }
        formel += "(";
        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
          if(j != 0) {
            formel += " OR ";
          }
          if(!wahrheitstafel[j][i]) {
            formel += variables[j];
          } else {
            formel += "NOT " + variables[j];
          }
        }
        formel += ")";
      }
    }
    return formel;
  }
  
  @Override
  public String toString() {
    return getAsString();
  }
  
}
