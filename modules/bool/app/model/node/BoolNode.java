package model.node;

import java.util.Set;

import model.tree.Assignment;

public interface BoolNode {

  /**
   * Evaluate this node recursively
   *
   * @param assignment
   *
   * @return
   */
  public boolean evaluate(Assignment assignment);

  /**
   * Returns the formula represented by this node as a string with optional
   * parantheses
   *
   * @param needsParanthesis
   *          - true, if there is a parent node
   * @return Representation of this formula as String
   */
  public String getAsString(boolean needsParanthesis);

  public Set<Character> getUsedVariables();

}
