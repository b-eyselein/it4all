package model.matching;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public abstract class Match<T> extends EvaluationResult {

  protected T arg1, arg2;

  public Match(FeedbackLevel theMinimalFL, T theArg1, T theArg2) {
    super(theMinimalFL, Success.NONE);
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
