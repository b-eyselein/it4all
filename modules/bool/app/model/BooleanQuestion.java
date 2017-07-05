package model;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BooleanQuestion {
  
  protected static final Random GENERATOR = new Random();
  
  public static final char SOLUTION_VARIABLE = 'z';
  public static final char LEARNER_VARIABLE = 'y';
  
  protected final Set<Character> variables;
  
  public BooleanQuestion(Set<Character> theVariables) {
    variables = theVariables;
  }
  
  public String getJoinedVariables() {
    return variables.stream().map(String::valueOf).collect(Collectors.joining(", "));
  }
  
  public int getNumberOfLines() {
    return (int) Math.pow(2, variables.size());
  }
  
  public Set<Character> getVariables() {
    return variables;
  }
}
