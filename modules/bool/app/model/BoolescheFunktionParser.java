package model;

import model.node.False;
import model.node.Node;
import model.node.NodeType;
import model.node.True;
import model.node.Variable;
import model.tree.BoolescheFunktionTree;

public class BoolescheFunktionParser {

  private BoolescheFunktionParser() {

  }

  public static BoolescheFunktionTree parse(String originalformel) throws BooleanParsingException {
    return new BoolescheFunktionTree(parseNode(originalformel));
  }

  public static Node parseNode(String formula) throws BooleanParsingException {
    formula = prepareFormula(formula);

    int parenthesisDepth = 0;
    String read = "";

    int highestOperatorPosition = -1;
    NodeType highestOperatorType = null;
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
          NodeType newOperatorType = NodeType.get(read);
          if(highestOperator == null || highestOperatorType.getPrecende() < newOperatorType.getPrecende()) {
            // FIXME: Operatorpräzedenz!
            // higheset operator found, ignore everything else
            highestOperator = read;
            highestOperatorType = NodeType.get(highestOperator);
            highestOperatorPosition = i - highestOperator.length() - 1;
          }
        }
        read = "";
        break;
      default:
        read += Character.toString(readChar);
        break;
      }
    }

    if(highestOperator == null) {
      // Kein Operator ==> Variable!
      if("1".equals(formula) || "true".equals(formula))
        return new True();
      else if("0".equals(formula) || "false".equals(formula))
        return new False();

      if(formula.length() > 1)
        throw new BooleanParsingException("Es gab einen Fehler beim Parsen ihrer Formel", formula);
      else
        return new Variable(formula.charAt(0));
    }

    String rightFormula = formula.substring(highestOperatorPosition + highestOperator.length() + 2);
    if("not".equals(highestOperator)) {
      return NodeType.NOT.instantiate(parseNode(rightFormula));
    } else {
      String leftFormula = formula.substring(0, highestOperatorPosition);
      NodeType type = NodeType.get(highestOperator);

      if(type == null)
        throw new BooleanParsingException("There is no operator defined.", highestOperator);

      Node left = parseNode(leftFormula);
      Node right = parseNode(rightFormula);

      return type.instantiate(left, right);
    }
  }

  private static String prepareFormula(String formula) {
    formula = formula.toLowerCase();
    formula = substituteGermanOperators(formula);

    // remove outer parantheses like in (a or b)
    formula = trimAndRemoveParantheses(formula);
    return formula;
  }

  /**
   * Substituiert alle deutschen Operatoren durch aequivalente englische
   * Operatoren.
   *
   * @return formel
   */
  private static String substituteGermanOperators(String formula) {
    String newFormula = formula;
    for(NodeType type: NodeType.values())
      newFormula = newFormula.replaceAll(type.getGermanOperator(), type.getEnglishOperator());
    return newFormula;
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
