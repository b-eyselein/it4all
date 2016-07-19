package model.bool;

import java.util.List;

import model.bool.tree.Assignment;

public class CreationQuestion extends BooleanQuestion {
  
  public static CreationQuestion generateNew() {
    // Get two or three variables a, b (and c)
    int anzVars = GENERATOR.nextInt(2) + 2;
    Character[] variables = new Character[anzVars];
    for(int i = 0; i < anzVars; i++)
      variables[i] = ALPHABET[i];
    
    // Generate random solutions for all assignments
    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    for(Assignment assign: assignments) {
      assign.setAssignment(SOLUTION_VARIABLE, GENERATOR.nextBoolean());
    }
    
    return new CreationQuestion(variables, assignments);
  }
  
  private List<Assignment> solutions;
  
  public CreationQuestion(Character[] theVariables, List<Assignment> theSolutions) {
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
