package model.boolescheAlgebra;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import model.boolescheAlgebra.BFTree.Assignment;

public class CreationQuestion {

  private static final Random GENERATOR = new Random();
  private static final char SOLUTION_VARIABLE = 'z';
  
  private final static char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  
  public static CreationQuestion generateNew() {
    // Get two or three variables a, b (and c)
    int anzVars = GENERATOR.nextInt(2) + 2;
    char[] variables = Arrays.copyOf(ALPHABET, anzVars);
    
    // Generate random solutions for all assignments
    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    for(Assignment assign: assignments) {
      assign.setAssignment(SOLUTION_VARIABLE, GENERATOR.nextBoolean());
    }
    
    return new CreationQuestion(variables, assignments);
  }
  
  private char[] vars;
  
  private List<Assignment> solutions;
  
  public CreationQuestion(char[] theVariables, List<Assignment> theSolutions) {
    vars = theVariables;
    solutions = theSolutions;
  }
  
  public String getJoinedVariables() {
    String ret = "";
    for(int i = 0; i < vars.length - 1; i++) {
      ret += vars[i] + ", ";
    }
    return ret + vars[vars.length - 1];
  }
  
  public int getNumberOfLines() {
    return (int) Math.pow(2, vars.length);
  }
  
  public List<Assignment> getSolutions() {
    return solutions;
  }
  
  public char getSolutionVariable() {
    return SOLUTION_VARIABLE;
  }
  
  public char[] getVariables() {
    return vars;
  }
  
}
