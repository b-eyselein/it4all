package model.tree;

public class AssignmentItem {
  
  private char variable;
  private boolean value = false;
  
  public AssignmentItem(char theVariable, boolean theValue) {
    variable = theVariable;
    value = theValue;
  }
  
  public boolean getValue() {
    return value;
  }
  
  public char getVariable() {
    return variable;
  }
  
  public void setValue(boolean theValue) {
    value = theValue;
  }
  
  @Override
  public String toString() {
    return variable + ":" + (value ? "1" : "0");
  }
  
}