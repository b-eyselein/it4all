package model.result;

import model.UmlExercise;
import model.UmlSolution;
import model.matcher.ClassMatcher;
import model.matcher.UmlClassMatch;
import model.matching.Matcher;
import model.uml.UmlClass;

public class ClassSelectionResult extends UmlResult {
  
  protected static final Matcher<UmlClass, UmlClassMatch> CLASS_MATCHER = new ClassMatcher(false);

  public ClassSelectionResult(UmlExercise exercise, UmlSolution learnerSol) {
    super(exercise);
    classResult = CLASS_MATCHER.match("Gewählte Klassen", learnerSol.getClasses(), exercise.getSolution().getClasses());
  }
  
}
