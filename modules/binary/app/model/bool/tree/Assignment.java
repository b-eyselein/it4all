package model.bool.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.bool.BooleanQuestion;

public class Assignment {

  private static class AssignmentItem {
    private char variable;
    private boolean value = false;

    public AssignmentItem(char theVariable, boolean theValue) {
      variable = theVariable;
      value = theValue;
    }

    public boolean getValue() {
      return value;
    }

    public char getVariable() {
      return variable;
    }

    public void setValue(boolean theValue) {
      value = theValue;
    }

    @Override
    public String toString() {
      return variable + ":" + (value ? "1" : "0");
    }

  }

  /**
   * Generates all possible assignments for this combination of variables
   * recursively
   *
   * @param variables
   *          the variables to generate the assignments for
   * @return all assignments for this combination of variables
   */
  public static List<Assignment> generateAllAssignments(Character[] variables) {
    if(variables.length == 0)
      throw new IllegalArgumentException("Cannot generate assignments for 0 variables!");

    char variable = variables[0];

    if(variables.length == 1) {
      // Catch recursive case of 1 variable
      Assignment falseAssignment = new Assignment(new AssignmentItem(variable, false));
      Assignment trueAssignment = new Assignment(new AssignmentItem(variable, true));
      return Arrays.asList(falseAssignment, trueAssignment);
    }

    List<Assignment> falseAssignments = generateAllAssignments(Arrays.copyOfRange(variables, 1, variables.length));
    List<Assignment> trueAssignments = generateAllAssignments(Arrays.copyOfRange(variables, 1, variables.length));

    for(Assignment assignment: falseAssignments)
      assignment.setAssignment(variable, false);

    for(Assignment assignment: trueAssignments)
      assignment.setAssignment(variable, true);

    ArrayList<Assignment> ret = new ArrayList<>();
    ret.addAll(falseAssignments);
    ret.addAll(trueAssignments);
    return ret;
  }

  private List<AssignmentItem> assignments = new LinkedList<>();

  public Assignment() {

  }

  private Assignment(AssignmentItem... items) {
    assignments = new LinkedList<>(Arrays.asList(items));
  }

  public boolean assignmentIsSet(char variable) {
    return getAssignmentItem(variable) != null;
  }

  public boolean getAssignment(char variable) {
    AssignmentItem item = getAssignmentItem(variable);

    if(item == null)
      // return false, if assignment is not defined
      return false;

    return item.getValue();
  }

  public char asChar(char Variable) {
    return getAssignment(Variable) == true ? '1' : '0';
  }

  public String getColor() {
    if(getAssignment(BooleanQuestion.LEARNER_VARIABLE) == getAssignment(BooleanQuestion.SOLUTION_VARIABLE))
      return "text-success";
    else
      return "text-danger";
  }

  public List<Character> getVariables() {
    return assignments.stream()
        .filter(a -> (a.getVariable() != BooleanQuestion.SOLUTION_VARIABLE
            && a.getVariable() != BooleanQuestion.LEARNER_VARIABLE))
        .map(a -> a.getVariable()).collect(Collectors.toList());
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
    List<String> assignmentStrings = assignments.stream().map(a -> a.toString()).collect(Collectors.toList());
    return String.join(",", assignmentStrings);
  }

  private AssignmentItem getAssignmentItem(char variable) {
    for(AssignmentItem item: assignments)
      if(item.getVariable() == variable)
        return item;
    return null;
  }

}
