package model.result;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlClass;
import model.UmlConnection;
import model.UmlExercise;
import model.UmlSolution;
import model.matcher.UmlClassMatch;
import model.matcher.UmlConnectionMatch;
import model.matching.MatchingResult;
import play.libs.Json;

public class DiagramDrawingResult extends UmlResult {

  private MatchingResult<UmlClass, UmlClassMatch> classResult;
  private MatchingResult<UmlConnection, UmlConnectionMatch> connectionResult;

  public DiagramDrawingResult(UmlExercise exercise, JsonNode userJson) {
    super(exercise);

    UmlSolution learnerSolution = Json.fromJson(userJson, UmlSolution.class);
    UmlSolution musterSolution = exercise.getSolution();

    classResult = CLASS_MATCHER.match(learnerSolution.getClasses(), musterSolution.getClasses());
    connectionResult = CONNECTION_MATCHER.match(learnerSolution.getConnections(), musterSolution.getConnections());
  }

  public MatchingResult<UmlClass, UmlClassMatch> getClassResult() {
    return classResult;
  }

  public MatchingResult<UmlConnection, UmlConnectionMatch> getConnectionResult() {
    return connectionResult;
  }

}
