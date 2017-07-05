package model.tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.BooleanQuestion;

public class Assignment {

  private List<AssignmentItem> assignments;

  public Assignment() {
    assignments = new LinkedList<>();
  }

  public Assignment(char var, boolean value) {
    this(new AssignmentItem(var, value));
  }

  private Assignment(AssignmentItem... items) {
    assignments = new LinkedList<>(Arrays.asList(items));
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
    Optional<AssignmentItem> item = getOptAI(variable);
    return (item.isPresent() && item.get().getValue());
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
    return assignments.stream()
        .filter(a -> a.getVariable() != BooleanQuestion.SOLUTION_VARIABLE
            && a.getVariable() != BooleanQuestion.LEARNER_VARIABLE)
        .map(AssignmentItem::getVariable).collect(Collectors.toList());
  }

  public boolean isSet(char variable) {
    return getAssignmentItem(variable) != null;
  }

  public void setAssignment(char variable, boolean value) {
    AssignmentItem item = getAssignmentItem(variable);
    if(item == null)
      assignments.add(new AssignmentItem(variable, value));
    else
      item.setValue(value);
  }

  @Override
  public String toString() {
    return assignments.stream()
        .filter(a -> a.getVariable() != BooleanQuestion.SOLUTION_VARIABLE
            && a.getVariable() != BooleanQuestion.LEARNER_VARIABLE)
        .map(AssignmentItem::toString).collect(Collectors.joining(","));
  }

  private AssignmentItem getAssignmentItem(char variable) {
    return getOptAI(variable).orElse(null);
  }

  private Optional<AssignmentItem> getOptAI(char variable) {
    return assignments.stream().filter(item -> item.getVariable() == variable).findFirst();
  }

}
