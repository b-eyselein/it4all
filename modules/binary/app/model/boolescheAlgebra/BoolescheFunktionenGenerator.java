package model.boolescheAlgebra;

import java.util.ArrayList;

import model.boolescheAlgebra.BFTree.*;

public class BoolescheFunktionenGenerator {
  
  private final static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
      
  /*
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
    // TODO: ob Tautologie oder Kontradiktion pruefen
    
    return bft;
  }
  
  /*
   * 
   */
  private static BFKnoten getRandomOperator(BFKnoten ka, BFKnoten kb) {
    if((int) Math.floor(Math.random() * 2) == 1) {
      ka = new BF_NOT(ka);
    }
    if((int) Math.floor(Math.random() * 2) == 1) {
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
}
