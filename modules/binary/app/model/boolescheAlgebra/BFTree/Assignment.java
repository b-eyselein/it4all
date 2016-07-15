package model.boolescheAlgebra.BFTree;

import java.util.HashMap;

public class Assignment {

  private HashMap<Character, Boolean> assignment = new HashMap<>();

  public boolean getAssignment(char variable) {
    return assignment.get(variable);
  }

  public void setAssignment(char variable, boolean value) {
    assignment.put(variable, value);
  }

}
