package model.boolescheAlgebra.BFTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import model.boolescheAlgebra.BoolescheFunktionParser;

public class BoolescheFunktionTree {
  
  /**
   * Gibt 1 oder 0 zum passenden Wahrheitswert zurueck.
   */
  private static char booleantochar(boolean b) {
    if(b) {
      return '1';
    } else {
      return '0';
    }
  }
  
  /**
   * Gibt 1 oder 0 zum passenden Wahrheitswert zurueck.
   */
  private static String booleantoString(boolean b) {
    if(b) {
      return "1";
    } else {
      return "0";
    }
  }
  
  private BFKnoten knoten;
  
  private BF_Variable[] vars;
  
  public BoolescheFunktionTree(BFKnoten k, BF_Variable... v) {
    this.knoten = k;
    this.vars = v;
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
    TreeSet<String> thisVars = new TreeSet<String>(Arrays.asList(this.getVariablen()));
    TreeSet<String> otherVars = new TreeSet<String>(Arrays.asList(otherBFT.getVariablen()));
    boolean variablen_unterschied = false;
    if(this.getAnzahlVariablen() != otherBFT.getAnzahlVariablen()) {
      variablen_unterschied = true;
    } else {
      for(String s: thisVars) {
        if(!otherVars.contains(s)) {
          variablen_unterschied = true;
        }
      }
    }
    if (variablen_unterschied) {
      thisVars.addAll(otherVars);
      String[] variablen = new String[thisVars.size()];
      variablen = (String[]) thisVars.toArray(variablen);
      tree1 = BoolescheFunktionParser.parse(tree1.toString(), variablen);
      tree2 = BoolescheFunktionParser.parse(tree2.toString(), variablen);
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
  
  /**
   * gibt Anzahl der Variablen zurueck
   */
  public int getAnzahlVariablen() {
    return vars.length;
  }
  
  /**
   * gibt eine Liste der Teilfunktion zurueck einschliesslich sich selbst
   */
  public List<BoolescheFunktionTree> getTeilformeln() {
    List<BoolescheFunktionTree> teilformeln = this.knoten.getTeilformeln(vars);
    return teilformeln;
  }
  
  /**
   * gibt sortierten String-Array mit Namen der Variablen zurueck
   */
  public String[] getVariablen() {
    String[] variablen = new String[vars.length];
    for(int i = 0; i < vars.length; i++) {
      variablen[i] = vars[i].toString();
    }
    return variablen;
  }
  
  /**
   * gibt den Teil der Tabelle der die Belegungen der Variablen enthaelt als
   * Char-Array zurueck. char[Spalte][Zeile] mit '1' fur wahr und '0' fuer
   * falsch
   */
  public char[][] getVariablenTabelle() {
    char[][] vtafel = new char[this.vars.length][(int) Math.pow(2, this.vars.length)];
    char[] zeile = new char[this.vars.length];
    for(int i = 0; i < zeile.length; i++) {
      zeile[i] = '0';
    }
    for(int i = 0; i < Math.pow(2, vars.length); i++) {
      for(int j = 0; j < zeile.length; j++) {
        vtafel[j][i] = zeile[j];
      }
      int k = vars.length - 1;
      if(zeile[vars.length - 1] == '1') {
        while(k > 0 && zeile[k] == '1') {
          zeile[k] = '0';
          k--;
        }
        zeile[k] = '1';
      } else {
        zeile[vars.length - 1] = '1';
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
    boolean[][] wtafel = new boolean[this.vars.length + 1][(int) Math.pow(2, this.vars.length)];
    boolean[] zeile = new boolean[this.vars.length];
    for(int i = 0; i < Math.pow(2, vars.length); i++) {
      for(int j = 0; j < zeile.length; j++) {
        wtafel[j][i] = zeile[j];
      }
      wtafel[this.vars.length][i] = this.getWert(zeile);
      int k = vars.length - 1;
      if(zeile[vars.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[vars.length - 1] = true;
      }
    }
    return wtafel;
  }
  
  /**
   * Gibt Wahrheitstafel als char Array zurueck. char[Spalte][Zeile] ;
   * Anzahl_der_Spalten = Anzahl_der_Variablen+1 ; Anzahl_der_Zeilen =
   * 2^Anzahl_der_Variablen ;
   */
  public char[][] getWahrheitstafelChar() {
    char[][] wtafel = new char[this.vars.length + 1][(int) Math.pow(2, this.vars.length)];
    boolean[] zeile = new boolean[this.vars.length];
    for(int i = 0; i < Math.pow(2, vars.length); i++) {
      for(int j = 0; j < zeile.length; j++) {
        wtafel[j][i] = booleantochar(zeile[j]);
      }
      wtafel[this.vars.length][i] = booleantochar(this.getWert(zeile));
      int k = vars.length - 1;
      if(zeile[vars.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[vars.length - 1] = true;
      }
    }
    return wtafel;
  }
  
  /**
   * Gibt Wahrheitstafel mit Beschriftung als String zurueck. (geeignet fuer
   * Komandozeile)
   */
  public String getWahrheitstafelString() {
    String s = "\n ";
    int[] spaltenbreite = new int[this.vars.length + 1];
    int spalte = 0;
    for(BF_Variable v: this.vars) {
      s += v.toString() + " | ";
      spaltenbreite[spalte] = s.length() - 2;
      spalte++;
    }
    s += this.toString() + "\n";
    spaltenbreite[spalte] = s.length() - 2;
    while(spalte > 0) {
      spaltenbreite[spalte] = spaltenbreite[spalte] - spaltenbreite[spalte - 1];
      spalte--;
    }
    for(int i = 0; i < spaltenbreite.length; i++) {
      for(int j = 0; j < spaltenbreite[i] - 1; j++) {
        s += "-";
      }
      if(i < spaltenbreite.length - 1) {
        s += "+";
      } else {
        s += "-\n";
      }
    }
    boolean[] zeile = new boolean[this.vars.length];
    for(int i = 0; i < Math.pow(2, vars.length); i++) {
      for(int j = 0; j < zeile.length; j++) {
        s += " " + booleantochar(zeile[j]);
        for(int h = 2; h < spaltenbreite[j] - 1; h++) {
          s += " ";
        }
        s += "|";
      }
      s += " " + booleantochar(this.getWert(zeile)) + "\n";
      int k = vars.length - 1;
      if(zeile[vars.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[vars.length - 1] = true;
      }
    }
    return s;
  }
  
  /**
   * gibt Vector mit den Werten des Ausdrucks zurueck
   */
  public boolean[] getWahrheitsVector() {
    boolean[] wvector = new boolean[(int) Math.pow(2, this.vars.length)];
    boolean[] zeile = new boolean[this.vars.length];
    for(int i = 0; i < Math.pow(2, vars.length); i++) {
      wvector[i] = this.getWert(zeile);
      int k = vars.length - 1;
      if(zeile[vars.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[vars.length - 1] = true;
      }
    }
    return wvector;
  }
  
  /**
   * gibt Vector mit den Werten als Char des Ausdrucks zurueck
   */
  public char[] getWahrheitsVectorChar() {
    char[] wvector = new char[(int) Math.pow(2, this.vars.length)];
    boolean[] zeile = new boolean[this.vars.length];
    for(int i = 0; i < Math.pow(2, vars.length); i++) {
      wvector[i] = booleantochar(this.getWert(zeile));
      int k = vars.length - 1;
      if(zeile[vars.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[vars.length - 1] = true;
      }
    }
    return wvector;
  }
  
  /**
   * gibt Vector mit den Werten als String des Ausdrucks zurueck
   */
  public String[] getWahrheitsVectorString() {
    String[] wvector = new String[(int) Math.pow(2, this.vars.length)];
    boolean[] zeile = new boolean[this.vars.length];
    for(int i = 0; i < Math.pow(2, vars.length); i++) {
      wvector[i] = booleantoString(this.getWert(zeile));
      int k = vars.length - 1;
      if(zeile[vars.length - 1]) {
        while(k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[vars.length - 1] = true;
      }
    }
    return wvector;
  }
  
  /**
   * Gibt den Wahrheitswert der Funktion zu dem uebergebenen bool Array zurueck.
   */
  public boolean getWert(boolean[] b) throws IllegalArgumentException {
    if(b.length != this.vars.length) {
      throw new IllegalArgumentException("Wrong number of boolean values. Expected " + this.getAnzahlVariablen()
          + " elements but there were " + b.length + " elements.");
    }
    for(int i = 0; i < this.vars.length; i++) {
      this.vars[i].setWert(b[i]);
    }
    return this.knoten.getWert();
  }
  
  /**
   * Gibt eine aequivalente Formel in kanonischer DNF als String zurueck
   */
  public String kanonischeDisjunktiveNormalform() {
    String formel = "";
    boolean[][] wahrheitstafel = this.getWahrheitstafelBoolean();
    String[] variablen = this.getVariablen();
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
            formel += variablen[j];
          } else {
            formel += "NOT " + variablen[j];
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
    boolean[][] wahrheitstafel = this.getWahrheitstafelBoolean();
    String[] variablen = this.getVariablen();
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
  
  /**
   * Gibt eine aequivalente Formel in vereinfachter DNF als String zurueck
   */
  public String kurzeDisjunktiveNormalform() {
    String formel = "";
    String[] variablen = this.getVariablen();
    boolean[][] wahrheitstafel = this.getWahrheitstafelBoolean();
    // suche wahre Eintraege in der Wahrheitstafel
    TreeSet<String> neueAusdruecke = new TreeSet<String>();
    for(int i = 0; i < wahrheitstafel[0].length; i++) {
      if(wahrheitstafel[wahrheitstafel.length - 1][i]) {
        String neuerAusdruck = "";
        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
          if(wahrheitstafel[j][i]) {
            neuerAusdruck += variablen[j];
          } else {
            neuerAusdruck += "NOT " + variablen[j];
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
  
  /**
   * Gibt eine aequivalente Formel in vereinfachter KNF als String zurueck
   */
  public String kurzeKonjunktiveNormalform() {
    String formel = "";
    String[] variablen = this.getVariablen();
    boolean[][] wahrheitstafel = this.getWahrheitstafelBoolean();
    // suche wahre Eintraege in der Wahrheitstafel
    TreeSet<String> neueAusdruecke = new TreeSet<String>();
    for(int i = 0; i < wahrheitstafel[0].length; i++) {
      if(!wahrheitstafel[wahrheitstafel.length - 1][i]) {
        String neuerAusdruck = "";
        for(int j = 0; j < wahrheitstafel.length - 1; j++) {
          if(!wahrheitstafel[j][i]) {
            neuerAusdruck += variablen[j];
          } else {
            neuerAusdruck += "NOT " + variablen[j];
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
  
  /**
   * Gibt boolesche Funktion als Sting zurueck.
   */
  @Override
  public String toString() {
    return knoten.toString();
  }
  
}
