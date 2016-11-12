package model;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import model.exercise.Exercise;

public abstract class BooleanQuestion implements Exercise {

  protected static final Random GENERATOR = new Random();
  protected static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

  public static final char SOLUTION_VARIABLE = 'z';
  public static final char LEARNER_VARIABLE = 'y';

  protected Character[] variables;

  public BooleanQuestion(Character[] theVariables) {
    variables = theVariables;
  }

  public String getJoinedVariables() {
    return Arrays.stream(variables).map(v -> Character.toString(v)).collect(Collectors.joining(", "));
  }

  public int getNumberOfLines() {
    return (int) Math.pow(2, variables.length);
  }

  public Character[] getVariables() {
    return variables;
  }
}
