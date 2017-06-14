package model.result;

import model.UmlExercise;
import model.UmlSolution;
import model.matcher.AssociationMatcher;
import model.matcher.UmlAssociationMatch;
import model.matching.GenericMatch;
import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.uml.UmlAssociation;
import model.uml.UmlImplementation;

public class DiagramDrawingResult extends UmlResult {
  
  private static final AssociationMatcher ASSOCIATION_MATCHER = new AssociationMatcher();
  private static final Matcher<UmlImplementation, Match<UmlImplementation>> IMPLEMENTATION_MATCHER = new Matcher<UmlImplementation, Match<UmlImplementation>>(
      UmlImplementation::equals) {
    
    @Override
    protected Match<UmlImplementation> instantiateMatch(UmlImplementation arg1, UmlImplementation arg2) {
      return new GenericMatch<>(arg1, arg2);
    }
  };
  
  private MatchingResult<UmlAssociation, UmlAssociationMatch> associationResult;
  private MatchingResult<UmlImplementation, Match<UmlImplementation>> implementationResult;
  
  public DiagramDrawingResult(UmlExercise exercise, UmlSolution learnerSol) {
    super(exercise);
    
    UmlSolution musterSol = exercise.getSolution();
    
    classResult = CLASS_MATCHER.match("Erstellte Klassen", learnerSol.getClasses(), musterSol.getClasses());
    
    associationResult = ASSOCIATION_MATCHER.match("Erstellte Assoziationen", learnerSol.getAssociations(),
        musterSol.getAssociations());
    implementationResult = IMPLEMENTATION_MATCHER.match("Erstellte Vererbungsbeziehungen",
        learnerSol.getImplementations(), musterSol.getImplementations());
  }
  
  public MatchingResult<UmlAssociation, UmlAssociationMatch> getAssociationResult() {
    return associationResult;
  }
  
  public MatchingResult<UmlImplementation, Match<UmlImplementation>> getImplementationResult() {
    return implementationResult;
  }
  
}
