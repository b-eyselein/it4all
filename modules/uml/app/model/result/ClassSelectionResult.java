package model.result;

import model.UmlExercise;
import model.UmlSolution;
import model.matching.Matcher;
import model.uml.UmlClass;
import model.umlmatcher.UmlClassMatcher;
import model.umlmatcher.UmlClassMatch;

public class ClassSelectionResult extends UmlResult {
  
  protected static final Matcher<UmlClass, UmlClassMatch> CLASS_MATCHER = new UmlClassMatcher(false);

  public ClassSelectionResult(UmlExercise exercise, UmlSolution learnerSol) {
    super(exercise);
    classResult = CLASS_MATCHER.match( learnerSol.getClasses(), exercise.getSolution().getClasses());
  }
  
}
