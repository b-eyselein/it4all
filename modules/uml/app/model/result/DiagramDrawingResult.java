package model.result;

import model.UmlExercise;
import model.UmlSolution;
import model.matcher.AssociationMatcher;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.uml.UmlAssociation;
import model.uml.UmlImplementation;

public class DiagramDrawingResult extends UmlResult {
  
  private static final AssociationMatcher ASSOCIATION_MATCHER = new AssociationMatcher();
  private static final Matcher<UmlImplementation> IMPLEMENTATION_MATCHER = new Matcher<>(UmlImplementation::equals);
  
  private MatchingResult<UmlAssociation> associationResult;
  private MatchingResult<UmlImplementation> implementationResult;
  
  public DiagramDrawingResult(UmlExercise exercise, UmlSolution learnerSol) {
    super(exercise);
    
    UmlSolution musterSol = exercise.getSolution();
    
    classResult = CLASS_MATCHER.match(learnerSol.getClasses(), musterSol.getClasses());
    
    associationResult = ASSOCIATION_MATCHER.match(learnerSol.getAssociations(), musterSol.getAssociations());
    implementationResult = IMPLEMENTATION_MATCHER.match(learnerSol.getImplementations(),
        musterSol.getImplementations());
  }
  
  public MatchingResult<UmlAssociation> getAssociationResult() {
    return associationResult;
  }
  
  public MatchingResult<UmlImplementation> getImplementationResult() {
    return implementationResult;
  }
  
}
