package model.result;

import model.UmlExercise;
import model.matcher.ClassMatcher;
import model.matcher.ConnectionMatcher;
import model.matching.StringMatcher;

public abstract class UmlResult {

  protected static final StringMatcher STRING_MATCHER = new StringMatcher();
  protected static final ClassMatcher CLASS_MATCHER = new ClassMatcher();
  protected static final ConnectionMatcher CONNECTION_MATCHER = new ConnectionMatcher();

  private UmlExercise exercise;

  public UmlResult(UmlExercise theExercise) {
    exercise = theExercise;
  }

  public UmlExercise getExercise() {
    return exercise;
  }

}
