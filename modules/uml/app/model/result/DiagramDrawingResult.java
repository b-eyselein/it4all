package model.result;

import model.UmlExercise;
import model.UmlSolution;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.uml.UmlAssociation;
import model.uml.UmlClass;
import model.uml.UmlImplementation;
import model.umlmatcher.UmlAssociationMatcher;
import model.umlmatcher.UmlClassMatcher;
import model.umlmatcher.UmlAssociationMatch;
import model.umlmatcher.UmlClassMatch;
import model.umlmatcher.UmlImplementationMatch;
import model.umlmatcher.UmlImplementationMatcher;

public class DiagramDrawingResult extends UmlResult {
  
  protected static final Matcher<UmlClass, UmlClassMatch> CLASS_MATCHER = new UmlClassMatcher(true);
  private static final UmlAssociationMatcher ASSOCIATION_MATCHER = new UmlAssociationMatcher();
  private static final UmlImplementationMatcher IMPLEMENTATION_MATCHER = new UmlImplementationMatcher();
  
  private MatchingResult<UmlAssociation, UmlAssociationMatch> associationResult;
  private MatchingResult<UmlImplementation, UmlImplementationMatch> implementationResult;
  
  public DiagramDrawingResult(UmlExercise exercise, UmlSolution learnerSol) {
    super(exercise);
    
    UmlSolution musterSol = exercise.getSolution();
    
    classResult = CLASS_MATCHER.match(learnerSol.getClasses(), musterSol.getClasses());
    
    associationResult = ASSOCIATION_MATCHER.match(learnerSol.getAssociations(), musterSol.getAssociations());
    implementationResult = IMPLEMENTATION_MATCHER.match(learnerSol.getImplementations(),
        musterSol.getImplementations());
  }
  
  public MatchingResult<UmlAssociation, UmlAssociationMatch> getAssociationResult() {
    return associationResult;
  }
  
  public MatchingResult<UmlImplementation, UmlImplementationMatch> getImplementationResult() {
    return implementationResult;
  }
  
}
