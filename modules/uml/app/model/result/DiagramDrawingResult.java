package model.result;

import model.UmlExercise;
import model.UmlSolution;
import model.matcher.AssociationMatcher;
import model.matcher.UmlAssociationMatch;
import model.matcher.UmlImplementationMatch;
import model.matcher.UmlImplementationMatcher;
import model.matching.MatchingResult;
import model.uml.UmlAssociation;
import model.uml.UmlImplementation;

public class DiagramDrawingResult extends UmlResult {

  private static final AssociationMatcher ASSOCIATION_MATCHER = new AssociationMatcher();
  private static final UmlImplementationMatcher IMPLEMENTATION_MATCHER = new UmlImplementationMatcher();

  private MatchingResult<UmlAssociation, UmlAssociationMatch> associationResult;
  private MatchingResult<UmlImplementation, UmlImplementationMatch> implementationResult;

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

  public MatchingResult<UmlImplementation, UmlImplementationMatch> getImplementationResult() {
    return implementationResult;
  }

}
