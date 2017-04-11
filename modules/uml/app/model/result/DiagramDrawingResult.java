package model.result;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlExercise;
import model.UmlSolution;
import model.matcher.ClassMatchingResult;
import model.matcher.ConnectionMatchingResult;
import play.libs.Json;

public class DiagramDrawingResult extends UmlResult {
  
  private ClassMatchingResult classResult;
  private ConnectionMatchingResult connectionResult;
  
  public DiagramDrawingResult(UmlExercise exercise, JsonNode userJson) {
    super(exercise);
    
    UmlSolution learnerSolution = Json.fromJson(userJson, UmlSolution.class);
    UmlSolution musterSolution = exercise.getSolution();
    
    classResult = CLASS_MATCHER.match(learnerSolution.getClasses(), musterSolution.getClasses());
    connectionResult = CONNECTION_MATCHER.match(learnerSolution.getConnections(), musterSolution.getConnections());
  }
  
  public ClassMatchingResult getClassResult() {
    return classResult;
  }
  
  public ConnectionMatchingResult getConnectionResult() {
    return connectionResult;
  }
  
}
