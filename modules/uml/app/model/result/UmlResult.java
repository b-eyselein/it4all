package model.result;

import model.UmlExercise;
import model.matcher.UmlClassMatch;
import model.matching.MatchingResult;
import model.uml.UmlClass;

public abstract class UmlResult {
  
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
