package model.result;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public abstract class Match<T> extends EvaluationResult {
  
  protected T arg1, arg2;
  
  public Match(T theArg1, T theArg2) {
    super(Success.NONE);
    arg1 = theArg1;
    arg2 = theArg2;
    analyze();
  }
  
  public abstract void analyze();
  
  public T getFirstArg() {
    return arg1;
  }
  
  public T getSecondArg() {
    return arg2;
  }
}
