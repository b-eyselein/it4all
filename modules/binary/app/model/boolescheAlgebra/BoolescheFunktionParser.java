package model.boolescheAlgebra;

import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;
import model.boolescheAlgebra.BFTree.False;
import model.boolescheAlgebra.BFTree.Node;
import model.boolescheAlgebra.BFTree.NodeType;
import model.boolescheAlgebra.BFTree.True;
import model.boolescheAlgebra.BFTree.Variable;

public class BoolescheFunktionParser {

  public static BoolescheFunktionTree parse(String originalformel, char[] variablen) throws IllegalArgumentException {
    return new BoolescheFunktionTree(parseNode(originalformel));
  }

  public static Node parseNode(String formula) {
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
      return NodeType.NOT.instantiate(parseNode(rightFormula));
    } else {
      String leftFormula = formula.substring(0, highestOperatorPosition);
      NodeType type = NodeType.get(highestOperator);

      if(type == null)
        throw new IllegalArgumentException("No operator is defined for: " + highestOperator);

      Node left = parseNode(leftFormula);
      Node right = parseNode(rightFormula);

      return type.instantiate(left, right);
    }
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
