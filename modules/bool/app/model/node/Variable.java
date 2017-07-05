package model.node;

import java.util.Set;
import java.util.TreeSet;

import model.tree.Assignment;

public class Variable implements Node {
  
  private char name;
  
  public Variable(char theName) {
    this.name = theName;
  }
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return assignment.get(name);
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return Character.toString(name);
  }
  
  @Override
  public Set<Character> getUsedVariables() {
    TreeSet<Character> ret = new TreeSet<>();
    ret.add(name);
    return ret;
  }
  
  public char getVariable() {
    return name;
  }
  
  @Override
  public String toString() {
    return Character.toString(name);
  }
  
}
