package model.result;

import model.UmlExercise;
import model.matcher.ClassMatcher;
import model.matcher.UmlClassMatch;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.uml.UmlClass;

public abstract class UmlResult {

  protected static final Matcher<UmlClass, UmlClassMatch> CLASS_MATCHER = new ClassMatcher();

  protected MatchingResult<UmlClass, UmlClassMatch> classResult;

  private UmlExercise exercise;

  public UmlResult(UmlExercise theExercise) {
    exercise = theExercise;
  }

  public MatchingResult<UmlClass, UmlClassMatch> getClassResult() {
    return classResult;
  }

  public UmlExercise getExercise() {
    return exercise;
  }
}
