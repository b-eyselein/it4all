package model.result;

import model.UmlExercise;
import model.UmlSolution;

public class ClassSelectionResult extends UmlResult {
  
  public ClassSelectionResult(UmlExercise exercise, UmlSolution learnerSol) {
    super(exercise);
    classResult = CLASS_MATCHER.match(learnerSol.getClasses(), exercise.getSolution().getClasses());
  }
  
}
