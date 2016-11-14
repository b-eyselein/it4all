package model;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.exercise.ExerciseIdentifier;
import model.tree.Assignment;

public class CreationQuestion extends BooleanQuestion {
  
  private List<Assignment> solutions;

  private String learnerSolution;

  public CreationQuestion(Character[] theVariables, List<Assignment> theSolutions) {
    super(theVariables);
    solutions = theSolutions;
  }
  
  public CreationQuestion(Character[] theVariables, List<Assignment> theSolutions, String theLearnerSolution) {
    this(theVariables, theSolutions);
    learnerSolution = theLearnerSolution;
  }
  
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
  
  public String getDisjunktiveNormalForm() {
    // @formatter:off
    List<String> formulas = solutions.stream()
        // Take all positive assignments
        .filter(as -> as.getAssignment(SOLUTION_VARIABLE))
        .map(as -> {
          List<String> vars = new LinkedList<>();
          for(char variable: as.getVariables())
            vars.add((as.getAssignment(variable) ? "NOT " : "") + variable);
          return"("+ String.join(" AND ", vars) + ")";
        })
        .collect(Collectors.toList());
    // @formatter:on
    return String.join(" OR ", formulas);
  }
  
  @Override
  public ExerciseIdentifier getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public String getKonjunktiveNormalForm() {
    // @formatter:off
    List<String> formulas = solutions.stream()
        // take all negative assignments
        .filter(as -> !as.getAssignment(SOLUTION_VARIABLE))
        .map(as -> {
          List<String> vars = new LinkedList<>();
          for(char variable: as.getVariables())
            vars.add((as.getAssignment(variable) ? "NOT " : "") + variable);
          return"("+ String.join(" OR ", vars) + ")";
        })
        .collect(Collectors.toList());
    // @formatter:on
    return String.join(" AND ", formulas);
  }
  
  public String getLearnerSolution() {
    return learnerSolution;
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }

  public List<Assignment> getSolutions() {
    return solutions;
  }

  public char getSolutionVariable() {
    return SOLUTION_VARIABLE;
  }

  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTitle() {
    // TODO Auto-generated method stub
    return null;
  }
  
}
