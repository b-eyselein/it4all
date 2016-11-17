package model.tree;

import java.util.LinkedList;
import java.util.List;

import model.BooleanQuestion;
import model.node.Node;

public class BoolescheFunktionTree {

  private Node rootNode;

  private List<Character> variables;

  public BoolescheFunktionTree(Node theRootNode) {
    this.rootNode = theRootNode;
    variables = new LinkedList<>(theRootNode.getUsedVariables());
  }

  private static char booleantochar(boolean b) {
    return b ? '1' : '0';
  }

  private static String booleantoString(boolean b) {
    return Character.toString(booleantochar(b));
  }

  public boolean evaluate(Assignment assignment) {
    return rootNode.evaluate(assignment);
  }

  /**
   * Gibt den Wahrheitswert der Funktion zu dem uebergebenen bool Array zurueck.
   */
  public boolean evaluate(boolean[] b) throws IllegalArgumentException {
    // TODO: boolean[] to Assignment!
    if(b.length != this.variables.size()) {
      throw new IllegalArgumentException("Wrong number of boolean values. Expected " + this.getAnzahlVariablen()
          + " elements but there were " + b.length + " elements.");
    }
    Assignment assignment = new Assignment();
    for(int i = 0; i < this.variables.size(); i++)
      assignment.setAssignment(variables.get(i), b[i]);

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
    return variables.size();
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
  public List<Character> getVariables() {
    return variables;
  }

  /**
   * Gibt Wahrheitstafel als bool Array zurueck. boolean[Spalte][Zeile] ;
   * Anzahl_der_Spalten = Anzahl_der_Variablen+1 ; Anzahl_der_Zeilen =
   * 2^Anzahl_der_Variablen ;
   */
  public boolean[][] getWahrheitstafelBoolean() {
    boolean[][] wtafel = new boolean[this.variables.size() + 1][(int) Math.pow(2, this.variables.size())];
    boolean[] zeile = new boolean[this.variables.size()];
    for(int i = 0; i < Math.pow(2, variables.size()); i++) {
      for(int j = 0; j < zeile.length; j++) {
        wtafel[j][i] = zeile[j];
      }
      wtafel[this.variables.size()][i] = this.evaluate(zeile);
      int k = variables.size() - 1;
      if(zeile[variables.size() - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[variables.size() - 1] = true;
      }
    }
    return wtafel;
  }

  /**
   * gibt Vector mit den Werten des Ausdrucks zurueck
   */
  public boolean[] getWahrheitsVector() {
    boolean[] wvector = new boolean[(int) Math.pow(2, this.variables.size())];
    boolean[] zeile = new boolean[this.variables.size()];
    for(int i = 0; i < Math.pow(2, variables.size()); i++) {
      wvector[i] = this.evaluate(zeile);
      int k = variables.size() - 1;
      if(zeile[variables.size() - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[variables.size() - 1] = true;
      }
    }
    return wvector;
  }

  /**
   * gibt Vector mit den Werten als String des Ausdrucks zurueck
   */
  public String[] getWahrheitsVectorString() {
    String[] wvector = new String[(int) Math.pow(2, this.variables.size())];
    boolean[] zeile = new boolean[this.variables.size()];
    for(int i = 0; i < Math.pow(2, variables.size()); i++) {
      wvector[i] = booleantoString(this.evaluate(zeile));
      int k = variables.size() - 1;
      if(zeile[variables.size() - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[variables.size() - 1] = true;
      }
    }
    return wvector;
  }

  @Override
  public String toString() {
    return getAsString();
  }

}
