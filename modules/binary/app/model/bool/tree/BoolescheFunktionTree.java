package model.bool.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import model.bool.BooleanQuestion;
import model.bool.BoolescheFunktionParser;
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

  /**
   * Vergleicht getWahrheitsVector() mit dem uebergebenen WahrheitsVector
   * (boolean-Array). Wirft Fehler wenn Vektoren (Arrays) unterschiedlich lang
   * sind.
   */
  public boolean compareBooleanArray(boolean[] otherBooleanArray) throws IllegalArgumentException {
    boolean[] thisBooleanArray = this.getWahrheitsVector();
    if(thisBooleanArray.length != otherBooleanArray.length) {
      throw new IllegalArgumentException("Array has wrong number of elements. Should have " + thisBooleanArray.length
          + " elements but has " + otherBooleanArray.length + " elements.");
    }
    for(int i = 0; i < thisBooleanArray.length; i++) {
      if(thisBooleanArray[i] ^ otherBooleanArray[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Vergeicht diesen BoolescheFunktionTree mit anderem BoolescheFunktionTree.
   * Wirft Fehler wenn die Variablen nicht uebereinstimmen.
   */
  public boolean compareBoolscheFormelTree(BoolescheFunktionTree otherBFT) throws IllegalArgumentException {
    BoolescheFunktionTree tree1 = this;
    BoolescheFunktionTree tree2 = otherBFT;

    TreeSet<Character> thisVars = new TreeSet<>();
    for(char c: getVariablen())
      thisVars.add(c);

    TreeSet<Character> otherVars = new TreeSet<>();
    for(char c: otherBFT.getVariablen())
      otherVars.add(c);

    boolean variablen_unterschied = false;
    if(this.getAnzahlVariablen() != otherBFT.getAnzahlVariablen()) {
      variablen_unterschied = true;
    } else {
      for(Character s: thisVars) {
        if(!otherVars.contains(s)) {
          variablen_unterschied = true;
        }
      }
    }
    if(variablen_unterschied) {
      thisVars.addAll(otherVars);
      char[] variablen = new char[thisVars.size()];
      int i = 0;
      Iterator<Character> iter = thisVars.iterator();
      while(iter.hasNext())
        variablen[i++] = iter.next().charValue();
      tree1 = BoolescheFunktionParser.parse(tree1.getAsString());
      tree2 = BoolescheFunktionParser.parse(tree2.getAsString());
    }
    boolean[] thisWahrheitsVector = tree1.getWahrheitsVector();
    boolean[] otherWahrheitsVector = tree2.getWahrheitsVector();
    for(int i = 0; i < thisWahrheitsVector.length; i++) {
      if(thisWahrheitsVector[i] ^ otherWahrheitsVector[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Vergleicht getWahrheitsVector() mit dem uebergebenen WahrheitsVector
   * (String-Array). Wirft Fehler wenn Vektoren (Arrays) unterschiedlich lang
   * sind und wenn der String-Array ungueltige Zeichen enthaelt.
   */
  public boolean compareStringArray(String[] otherStringArray) throws IllegalArgumentException {
    boolean[] thisWahrheitsVector = this.getWahrheitsVector();
    if(thisWahrheitsVector.length != otherStringArray.length) {
      throw new IllegalArgumentException("Array has wrong number of elements. Should have " + thisWahrheitsVector.length
          + " elements but has " + otherStringArray.length + " elements.");
    }
    boolean[] otherWahrheitsVector = new boolean[otherStringArray.length];
    for(int i = 0; i < otherStringArray.length; i++) {
      if(otherStringArray[i].trim().equals("1")) {
        otherWahrheitsVector[i] = true;
      } else if(otherStringArray[i].trim().equals("0")) {
        otherWahrheitsVector[i] = false;
      } else {
        throw new IllegalArgumentException("at line " + (i + 1) + " unknown value: \"" + otherStringArray[i] + "\"");
      }
    }
    for(int i = 0; i < thisWahrheitsVector.length; i++) {
      if(thisWahrheitsVector[i] ^ otherWahrheitsVector[i]) {
        return false;
      }
    }
    return true;
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
  public Character[] getVariablen() {
    return variables;
  }

  /**
   * gibt den Teil der Tabelle der die Belegungen der Variablen enthaelt als
   * Char-Array zurueck. char[Spalte][Zeile] mit '1' fur wahr und '0' fuer
   * falsch
   */
  public char[][] getVariablenTabelle() {
    char[][] vtafel = new char[this.variables.length][(int) Math.pow(2, this.variables.length)];
    char[] zeile = new char[this.variables.length];
    for(int i = 0; i < zeile.length; i++) {
      zeile[i] = '0';
    }
    for(int i = 0; i < Math.pow(2, variables.length); i++) {
      for(int j = 0; j < zeile.length; j++) {
        vtafel[j][i] = zeile[j];
      }
      int k = variables.length - 1;
      if(zeile[variables.length - 1] == '1') {
        while(k > 0 && zeile[k] == '1') {
          zeile[k] = '0';
          k--;
        }
        zeile[k] = '1';
      } else {
        zeile[variables.length - 1] = '1';
      }
    }
    return vtafel;
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
