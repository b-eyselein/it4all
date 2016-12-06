package model;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import model.exercise.Exercise;

public abstract class BooleanQuestion extends Exercise {

  protected static final Random GENERATOR = new Random();
  protected static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

  public static final char SOLUTION_VARIABLE = 'z';
  public static final char LEARNER_VARIABLE = 'y';

  protected List<Character> variables;

  public BooleanQuestion(List<Character> theVariables) {
    variables = theVariables;
  }
  
  public String getJoinedVariables() {
    return variables.stream().map(v -> Character.toString(v)).collect(Collectors.joining(", "));
  }

  public int getNumberOfLines() {
    return (int) Math.pow(2, variables.size());
  }

  public List<Character> getVariables() {
    return variables;
  }
}
