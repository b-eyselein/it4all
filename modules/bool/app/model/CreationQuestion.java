package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreationQuestion extends BooleanQuestion {

  private List<Assignment> solutions;

  public CreationQuestion(Set<Character> theVariables, List<Assignment> theSolutions) {
    super(theVariables);
    solutions = theSolutions;
  }

  public static CreationQuestion generateNew() {
    // Get two or three variables a, b (and c)
    int anzVars = GENERATOR.nextInt(2) + 2;
    Set<Character> variables = IntStream.range('a', 'a' + anzVars).mapToObj(i -> (char) i).collect(Collectors.toSet());

    // Generate random solutions for all assignments
    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    assignments.forEach(a -> a.setAssignment(SOLUTION_VARIABLE, GENERATOR.nextBoolean()));

    return new CreationQuestion(variables, assignments);
  }

  public String getDisjunktiveNormalForm() {
    // @formatter:off
    return solutions.stream()
        // Take all positive assignments
        .filter(as -> as.get(SOLUTION_VARIABLE))
        .map(as -> {
          List<String> vars = new LinkedList<>();
          for(char variable: as.getVariables())
            vars.add((as.get(variable) ? "NOT " : "") + variable);
          return"("+ String.join(" AND ", vars) + ")";
        })
        .collect(Collectors.joining(" OR "));
    // @formatter:on
  }

  public String getKonjunktiveNormalForm() {
    return solutions.stream()
        // take all negative assignments
        .filter(as -> !as.get(SOLUTION_VARIABLE)).map(as -> {
          List<String> vars = new LinkedList<>();
          for(char variable: as.getVariables())
            vars.add((as.get(variable) ? "NOT " : "") + variable);
          return "(" + String.join(" OR ", vars) + ")";
        }).collect(Collectors.joining(" AND "));
  }

  public List<Assignment> getSolutions() {
    return solutions;
  }

  public char getSolutionVariable() {
    return SOLUTION_VARIABLE;
  }

}
