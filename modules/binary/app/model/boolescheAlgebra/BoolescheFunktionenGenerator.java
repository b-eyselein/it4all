package model.boolescheAlgebra;

import java.util.ArrayList;

import model.boolescheAlgebra.BFTree.*;

public class BoolescheFunktionenGenerator {
  
  private final static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
      
  /**
   * Liefert einen zufaelligen BoolescheFunktionTree mit 2 bis 4 Variablen.
   */
  public static BoolescheFunktionTree neueBoolescheFunktion() {
    int vars = (int) Math.floor(Math.random() * 3 + 2);
    ArrayList<BFKnoten> knoten = new ArrayList<BFKnoten>();
    BF_Variable[] v = new BF_Variable[vars];
    for(int i = 0; i < v.length; i++) {
      v[i] = new BF_Variable("" + alphabet[i]);
      knoten.add(v[i]);
    }
    for(int i = 0; i < (int) Math.floor(Math.random() * 6); i++) {
      knoten.add(v[(int) Math.floor(Math.random() * v.length)]);
    }
    while(knoten.size() > 1) {
      int indexA = (int) Math.floor(Math.random() * knoten.size());
      BFKnoten knotenA = knoten.get(indexA);
      knoten.remove(indexA);
      int indexB = (int) Math.floor(Math.random() * knoten.size());
      BFKnoten knotenB = knoten.get(indexB);
      knoten.remove(indexB);
      knoten.add(getRandomOperator(knotenA, knotenB));
    }
    BFKnoten k = knoten.get(0);
    BoolescheFunktionTree bft = new BoolescheFunktionTree(k, v);
    if (checkTautologie(bft)) {
      return neueBoolescheFunktion();
    } else {
      return bft;
    }
  }
  
  /**
   * Liefert einen zufaelligen BoolescheFunktionTree mit minVars bis maxVars Variablen. (maximale Anzahl ist zusaetzlich durch das Alphabet begrenzt)
   */
  public static BoolescheFunktionTree neueBoolescheFunktion(int minVars, int maxVars) {
    
    if (minVars<1 || maxVars<1) {
      throw new IllegalArgumentException("Die minimale Anzahl("+minVars+") und die maximale Anzahl("+maxVars+") der Variablen m\u00fcssen gr\u00f6\u00dfer als 0 sein.");
    }
    if (minVars>maxVars) {
      throw new IllegalArgumentException("die minimale Anzahl der Variablen("+minVars+") ist gr\u00f6\u00dfer als die maximale Anzahl der Variablen("+maxVars+").");
    }
    if (maxVars>alphabet.length) {
      throw new IllegalArgumentException("die maximale Anzahl der Variablen("+maxVars+") \u00fcbersteigt die Gr\u00f6\u00dfe des vordefinierten Alphabetes("+alphabet.length+").");
    }
    int vars = (int) Math.floor(Math.random() * (maxVars-minVars+1) + minVars);
    ArrayList<BFKnoten> knoten = new ArrayList<BFKnoten>();
    BF_Variable[] v = new BF_Variable[vars];
    for(int i = 0; i < v.length; i++) {
      v[i] = new BF_Variable("" + alphabet[i]);
      knoten.add(v[i]);
    }
    for(int i = 0; i < (int) Math.floor(Math.random() * maxVars); i++) {
      knoten.add(v[(int) Math.floor(Math.random() * v.length)]);
    }
    while(knoten.size() > 1) {
      int indexA = (int) Math.floor(Math.random() * knoten.size());
      BFKnoten knotenA = knoten.get(indexA);
      knoten.remove(indexA);
      int indexB = (int) Math.floor(Math.random() * knoten.size());
      BFKnoten knotenB = knoten.get(indexB);
      knoten.remove(indexB);
      knoten.add(getRandomOperator(knotenA, knotenB));
    }
    BFKnoten k;
    if((int) Math.floor(Math.random() * 4) == 1) {
      k = new BF_NOT(knoten.get(0));
    } else {
      k = knoten.get(0);
    }
    BoolescheFunktionTree bft = new BoolescheFunktionTree(k, v);
    if (checkTautologie(bft)) {
      return neueBoolescheFunktion(minVars, maxVars);
    } else {
      return bft;
    }
  }
  
  /**
   *  40% AND; 40% OR; 20% XOR; zusaetzlich 33% NOT jeweils bei dem linken und rechten Knoten
   */
  private static BFKnoten getRandomOperator(BFKnoten ka, BFKnoten kb) {
    if((int) Math.floor(Math.random() * 3) == 2) {
      ka = new BF_NOT(ka);
    }
    if((int) Math.floor(Math.random() * 3) == 2) {
      kb = new BF_NOT(kb);
    }
    int temp_op = (int) Math.floor(Math.random() * 5);
    BFKnoten operator;
    if(temp_op < 2) {
      operator = new BF_AND(ka, kb);
    } else if(temp_op < 4) {
      operator = new BF_OR(ka, kb);
    } else {
      operator = new BF_XOR(ka, kb);
    }
    return operator;
  }
  
  /**
   * gibt true zurueck wenn bft immer wahr oder immer falsch ist (Tautologie oder Kontradiktion ist).
   */
  private static boolean checkTautologie(BoolescheFunktionTree bft) {
    boolean b[] = bft.getWahrheitsVector();
    boolean contains_true = false;
    boolean contains_false = false;
    for (int i = 0; i < b.length; i++) {
      if (contains_true && contains_false) {
        return false;
      } else if (b[i] == true) {
        contains_true = true;
      } else if (b[i] == false) {
        contains_false = true;
      }
    }
    if (contains_true && contains_false) {
      return false;
    }
    return true;
  }
  
}