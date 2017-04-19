package model.result;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlAssociation;
import model.UmlClass;
import model.UmlExercise;
import model.UmlImplementation;
import model.UmlSolution;
import model.matching.MatchingResult;
import play.libs.Json;

public class DiagramDrawingResult extends UmlResult {

  private MatchingResult<UmlClass> classResult;
  private MatchingResult<UmlAssociation> associationResult;
  private MatchingResult<UmlImplementation> implementationResult;

  public DiagramDrawingResult(UmlExercise exercise, JsonNode userJson) {
    super(exercise);

    UmlSolution learnerSol = Json.fromJson(userJson, UmlSolution.class);
    UmlSolution musterSol = exercise.getSolution();

    classResult = CLASS_MATCHER.match(learnerSol.classes, musterSol.classes);
    associationResult = ASSOCIATION_MATCHER.match(learnerSol.associations, musterSol.associations);
    implementationResult = IMPLEMENTATION_MATCHER.match(learnerSol.implementations, musterSol.implementations);
  }

  public MatchingResult<UmlAssociation> getAssociationResult() {
    return associationResult;
  }

  public MatchingResult<UmlClass> getClassResult() {
    return classResult;
  }

  public MatchingResult<UmlImplementation> getImplementationResult() {
    return implementationResult;
  }

}
