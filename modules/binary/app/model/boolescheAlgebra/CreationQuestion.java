package model.boolescheAlgebra;

import java.util.Arrays;
import java.util.List;

import model.boolescheAlgebra.BFTree.Assignment;

public class CreationQuestion extends BooleanQuestion {
  
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
  
  private List<Assignment> solutions;
  
  public CreationQuestion(char[] theVariables, List<Assignment> theSolutions) {
    super(theVariables);
    solutions = theSolutions;
  }
  
  public List<Assignment> getSolutions() {
    return solutions;
  }
  
  public char getSolutionVariable() {
    return SOLUTION_VARIABLE;
  }
  
}
