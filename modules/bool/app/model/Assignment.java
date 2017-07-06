package model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Assignment {

  private Map<Character, Boolean> assignments = new HashMap<>();

  public Assignment(char var, boolean value) {
    assignments.put(var, value);
  }

  public Assignment(Map<Character, Boolean> items) {
    // Constructor for test, used with <b>IMMUTABLE</b>Map
    assignments.putAll(items);
  }

  public static List<Assignment> generateAllAssignments(Collection<Character> variables) {
    if(variables.isEmpty())
      throw new IllegalArgumentException("Cannot generate assignments for 0 variables!");

    SortedSet<Character> distinctVars = new TreeSet<>(variables);
    return generateAllAssignments(new LinkedList<>(distinctVars));

  }

  /**
   * Generates all possible assignments for this combination of variables
   * recursively
   *
   * @param variables
   *          the variables to generate the assignments for
   * @return all assignments for this combination of variables
   */
  private static List<Assignment> generateAllAssignments(List<Character> variables) {
    char variable = variables.get(0);

    if(variables.size() == 1)
      return Arrays.asList(new Assignment(variable, false), new Assignment(variable, true));

    List<Assignment> falseAssignments = generateAllAssignments(variables.subList(1, variables.size()));
    falseAssignments.forEach(a -> a.setAssignment(variable, false));

    List<Assignment> trueAssignments = generateAllAssignments(variables.subList(1, variables.size()));
    trueAssignments.forEach(a -> a.setAssignment(variable, true));

    return Stream.concat(falseAssignments.stream(), trueAssignments.stream()).collect(Collectors.toList());
  }

  public char asChar(char variable) {
    return get(variable) ? '1' : '0';
  }

  public boolean get(char variable) {
    return (assignments.containsKey(variable) && assignments.get(variable));
  }

  public String getAssignmentsForJson() {
    return toString();
  }

  public String getColor() {
    if(get(BooleanQuestion.LEARNER_VARIABLE) == get(BooleanQuestion.SOLUTION_VARIABLE))
      return "success";
    else
      return "danger";
  }

  public char getLearnerValue() {
    return asChar(BooleanQuestion.LEARNER_VARIABLE);
  }

  public List<Character> getVariables() {
    return assignments.entrySet().stream()
        .filter(a -> a.getKey() != BooleanQuestion.SOLUTION_VARIABLE && a.getKey() != BooleanQuestion.LEARNER_VARIABLE)
        .map(Entry::getKey).collect(Collectors.toList());
  }

  public boolean isSet(char variable) {
    return assignments.get(variable) != null;
  }

  public void setAssignment(char variable, boolean value) {
    assignments.put(variable, value);
  }

  @Override
  public String toString() {
    return assignments.entrySet().stream()
        .filter(a -> a.getKey() != BooleanQuestion.SOLUTION_VARIABLE && a.getKey() != BooleanQuestion.LEARNER_VARIABLE)
        .map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", "));
  }

}
