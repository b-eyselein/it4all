package model.boolescheAlgebra;

import java.util.Random;

public abstract class BooleanQuestion {

  protected static final Random GENERATOR = new Random();
  protected static final char SOLUTION_VARIABLE = 'z';

  protected final static char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

  protected char[] variables;

  public BooleanQuestion(char[] theVariabless) {
    variables = theVariabless;
  }

  public String getJoinedVariables() {
    String ret = "";
    for(int i = 0; i < variables.length - 1; i++) {
      ret += variables[i] + ", ";
    }
    return ret + variables[variables.length - 1];
  }

  public int getNumberOfLines() {
    return (int) Math.pow(2, variables.length);
  }

  public char[] getVariables() {
    return variables;
  }
}
