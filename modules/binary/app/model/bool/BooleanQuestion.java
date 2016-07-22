package model.bool;

import java.util.Random;

public abstract class BooleanQuestion {
  
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
    String ret = "";
    for(int i = 0; i < variables.length - 1; i++) {
      ret += variables[i] + ",";
    }
    return ret + variables[variables.length - 1];
  }
  
  public int getNumberOfLines() {
    return (int) Math.pow(2, variables.length);
  }
  
  public Character[] getVariables() {
    return variables;
  }
}
