package model;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.node.False;
import model.node.Node;
import model.node.NodeType;
import model.node.True;
import model.node.Variable;
import model.token.Token;
import model.tree.BoolescheFunktionTree;

public class BoolescheFunktionParser {

  private BoolescheFunktionParser() {

  }

  public static BoolescheFunktionTree parse(String originalformel) throws BooleanParsingException {
    return new BoolescheFunktionTree(parseNode(originalformel));
  }

  public static Node parseNew(String formulaToParse) throws BooleanParsingException {
    List<Token> tokens = tokenize(formulaToParse);

    // TODO: String to token...
    List<Token> groupedTokens = groupTokens(tokens);

    return buildTreeFromTokens(groupedTokens);
  }

  public static Node parseNode(String formulaToParse) throws BooleanParsingException {
    String formula = prepareFormula(formulaToParse);

    int parenthesisDepth = 0;
    StringBuilder read = new StringBuilder();

    int highestOperatorPosition = -1;
    NodeType highestOperatorType = null;
    String highestOperator = null;
    try {
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
            NodeType newOperatorType = highestOperator == null ? null : NodeType.valueOf(highestOperator.toUpperCase());
            if(highestOperator == null || highestOperatorType == null
                || highestOperatorType.getPrecende() < newOperatorType.getPrecende()) {
              // FIXME: Operatorpräzedenz!
              // higheset operator found, ignore everything else
              highestOperator = read.toString();
              highestOperatorType = NodeType.valueOf(highestOperator.toUpperCase());
              highestOperatorPosition = i - highestOperator.length() - 1;
            }
          }
          read = new StringBuilder();
          break;
        default:
          read.append(readChar);
          break;
        }
      }
    } catch (IllegalArgumentException e) {
      throw new BooleanParsingException("", formulaToParse, e);
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

    if("not".equals(highestOperator))
      return NodeType.NOT.instantiate(parseNode(rightFormula));

    String leftFormula = formula.substring(0, highestOperatorPosition);
    NodeType type = NodeType.valueOf(highestOperator.toUpperCase());

    if(type == null)
      throw new BooleanParsingException("There is no operator defined.", highestOperator);

    Node left = parseNode(leftFormula);
    Node right = parseNode(rightFormula);

    return type.instantiate(left, right);
  }

  private static List<Token> groupTokens(List<Token> tokens) {
    // TODO Auto-generated method stub
    return null;
  }

  private static String prepareFormula(String formula) {
    String newFormula = formula.toLowerCase();
    newFormula = substituteGermanOperators(newFormula);

    // remove outer parantheses like in (a or b)
    newFormula = trimAndRemoveParantheses(newFormula);
    return newFormula;
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

  private static String trimAndRemoveParantheses(String formulaToChange) {
    String formula = formulaToChange.trim();

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

  protected static Node buildTreeFromTokens(List<Token> tokens) {
    for(Token token: tokens) {

    }
    // TODO Auto-generated method stub
    return null;
  }

  protected static List<Token> tokenize(String formulaToParse) {
    List<String> strs = new LinkedList<>();
    StringBuilder read = new StringBuilder();
    for(char c: formulaToParse.toCharArray()) {
      switch(c) {
      case '(':
      case ')':
        if(!read.toString().isEmpty())
          strs.add(read.toString());
        strs.add(String.valueOf(c));
        read = new StringBuilder();
        break;
      case ' ':
        strs.add(read.toString());
        read = new StringBuilder();
        break;
      default:
        read.append(c);
        break;
      }
    }
    if(!read.toString().isEmpty())
      strs.add(read.toString());

    return strs.stream().map(Token::toToken).collect(Collectors.toList());
  }

}
