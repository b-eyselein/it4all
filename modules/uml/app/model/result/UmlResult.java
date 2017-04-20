package model.result;

import model.UmlExercise;
import model.matcher.ClassMatcher;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.uml.UmlClass;

public abstract class UmlResult {

  protected static final Matcher<UmlClass> CLASS_MATCHER = new ClassMatcher();
  protected MatchingResult<UmlClass> classResult;

  private UmlExercise exercise;

  public UmlResult(UmlExercise theExercise) {
    exercise = theExercise;
  }

  public MatchingResult<UmlClass> getClassResult() {
    return classResult;
  }

  public UmlExercise getExercise() {
    return exercise;
  }
}
