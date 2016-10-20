package model.bool;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import model.bool.node.Node;
import model.bool.node.Not;
import model.bool.node.Variable;
import model.bool.node.And;
import model.bool.node.Or;
import model.bool.node.Xor;
import model.bool.tree.BoolescheFunktionTree;

public class BoolescheFunktionenGenerator {

  private static final int MIN_VARS = 2;
  private static final int MAX_VARS = 3;

  private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

  private BoolescheFunktionenGenerator() {
    
  }
  
  /**
   * Liefert einen zufaelligen BoolescheFunktionTree mit MIN_VARS bis MAX_VARS
   * Variablen.
   */
  public static BoolescheFunktionTree neueBoolescheFunktion() {
    return neueBoolescheFunktion(MIN_VARS, MAX_VARS);
  }

  /**
   * Liefert einen zufaelligen BoolescheFunktionTree mit minVars bis maxVars
   * Variablen. (maximale Anzahl ist zusaetzlich durch das Alphabet begrenzt)
   */
  public static BoolescheFunktionTree neueBoolescheFunktion(int minVars, int maxVars) {
    if(minVars < 1 || maxVars < 1)
      throw new IllegalArgumentException("Die minimale Anzahl(" + minVars + ") und die maximale Anzahl(" + maxVars
          + ") der Variablen m\u00fcssen gr\u00f6\u00dfer als 0 sein.");

    if(minVars > maxVars)
      throw new IllegalArgumentException("Die minimale Anzahl der Variablen(" + minVars
          + ") muss gr\u00f6\u00dfer als die maximale Anzahl der Variablen(" + maxVars + ") sein.");

    if(maxVars > ALPHABET.length)
      throw new IllegalArgumentException("Die maximale Anzahl der Variablen(" + maxVars
          + ") \u00fcbersteigt die Gr\u00f6\u00dfe des vordefinierten Alphabetes(" + ALPHABET.length + ").");

    int numOfVariables = ThreadLocalRandom.current().nextInt(MIN_VARS, MAX_VARS + 1);

    ArrayList<Node> knoten = new ArrayList<>();
    Variable[] variables = new Variable[numOfVariables];
    for(int i = 0; i < variables.length; i++) {
      variables[i] = new Variable(ALPHABET[i]);
      knoten.add(variables[i]);
    }

    for(int i = 0; i < (int) Math.floor(Math.random() * maxVars); i++) {
      knoten.add(variables[(int) Math.floor(Math.random() * variables.length)]);
    }
    while(knoten.size() > 1) {
      int indexA = (int) Math.floor(Math.random() * knoten.size());
      Node knotenA = knoten.get(indexA);
      knoten.remove(indexA);
      int indexB = (int) Math.floor(Math.random() * knoten.size());
      Node knotenB = knoten.get(indexB);
      knoten.remove(indexB);
      knoten.add(getRandomOperator(knotenA, knotenB));
    }
    Node rootNode;
    if((int) Math.floor(Math.random() * 4) == 1) {
      rootNode = new Not(knoten.get(0));
    } else {
      rootNode = knoten.get(0);
    }
    BoolescheFunktionTree bft = new BoolescheFunktionTree(rootNode);
    if(checkTautologie(bft)) {
      return neueBoolescheFunktion(minVars, maxVars);
    } else {
      return bft;
    }
  }

  /**
   * gibt true zurueck wenn bft immer wahr oder immer falsch ist (Tautologie
   * oder Kontradiktion ist).
   */
  private static boolean checkTautologie(BoolescheFunktionTree bft) {
    boolean b[] = bft.getWahrheitsVector();
    boolean containsTrue = false;
    boolean containsFalse = false;
    for(int i = 0; i < b.length; i++) {
      if(containsTrue && containsFalse) {
        return false;
      } else if(b[i]) {
        containsTrue = true;
      } else if(!b[i]) {
        containsFalse = true;
      }
    }
    if(containsTrue && containsFalse) {
      return false;
    }
    return true;
  }

  /**
   * 40% AND; 40% OR; 20% XOR; zusaetzlich 33% NOT jeweils bei dem linken und
   * rechten Knoten
   */
  private static Node getRandomOperator(Node ka, Node kb) {
    if((int) Math.floor(Math.random() * 3) == 2) {
      ka = new Not(ka);
    }
    if((int) Math.floor(Math.random() * 3) == 2) {
      kb = new Not(kb);
    }
    int tempOp = (int) Math.floor(Math.random() * 5);
    Node operator;
    if(tempOp < 2) {
      operator = new And(ka, kb);
    } else if(tempOp < 4) {
      operator = new Or(ka, kb);
    } else {
      operator = new Xor(ka, kb);
    }
    return operator;
  }

}