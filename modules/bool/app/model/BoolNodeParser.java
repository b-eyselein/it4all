package model;

import model.node.BoolNode;
import model.node.Constant;
import model.node.NodeType;
import model.node.Variable;

public class BoolNodeParser {
  
  private static class OperatorPosition {
    
    private final NodeType type;
    private final int position;
    
    public OperatorPosition(NodeType theType, int thePosition) {
      type = theType;
      position = thePosition;
    }
    
    public int getPosition() {
      return position;
    }
    
    public NodeType getType() {
      return type;
    }
  }
  
  private BoolNodeParser() {
    
  }
  
  public static BoolNode parse(String formulaToParse) throws CorrectionException {
    String formula = prepareFormula(formulaToParse);
    
    OperatorPosition highestOper = findHighestOperator(formula);
    
    if(highestOper == null)
      return handleVarOrConst(formula);
    
    String rightFormula = formula.substring(highestOper.getPosition() + highestOper.getType().name().length() + 2);
    String leftFormula = formula.substring(0, highestOper.getPosition());
    
    return highestOper.getType().instantiate(parse(leftFormula), parse(rightFormula));
  }
  
  private static OperatorPosition findHighestOperator(String formula) throws CorrectionException {
    int parenthesisDepth = 0;
    StringBuilder read = new StringBuilder();
    OperatorPosition highestOper = null;
    
    for(int i = 0; i < formula.length(); i++) {
      char readChar = formula.charAt(i);
      
      switch(readChar) {
      case '(':
        parenthesisDepth++;
        read = new StringBuilder();
        break;
      case ')':
        parenthesisDepth--;
        read = new StringBuilder();
        break;
      case ' ':
        String toHandle = read.toString();
        read = new StringBuilder();
        
        if(toHandle.isEmpty() || toHandle.length() < 2 || parenthesisDepth > 0)
          break;
        
        NodeType newOperatorType;

        try {
          newOperatorType = NodeType.valueOf(toHandle.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new CorrectionException(formula,
              "Could not identify operator \"" + toHandle + "\" at position " + i + " in formula " + formula, e);
        }

        if(highestOper == null || highestOper.getType().getPrecende() < newOperatorType.getPrecende())
          highestOper = new OperatorPosition(newOperatorType, i - newOperatorType.name().length());
        break;
      default:
        read.append(readChar);
        break;
      }
    }
    return highestOper;
  }
  
  private static BoolNode handleVarOrConst(String formula) throws CorrectionException {
    if("1".equals(formula) || "true".equals(formula))
      return Constant.TRUE;
    
    if("0".equals(formula) || "false".equals(formula))
      return Constant.FALSE;
    
    if(formula.length() == 1)
      return new Variable(formula.charAt(0));
    
    throw new CorrectionException(formula, "Es gab einen Fehler beim Parsen ihrer Formel");
    
  }
  
  private static String prepareFormula(String formula) {
    String newFormula = substituteGermanOperators(formula.toLowerCase());
    
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
  
}
