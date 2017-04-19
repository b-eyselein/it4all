package model.result;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlClass;
import model.UmlExercise;
import model.UmlSolution;
import model.matching.MatchingResult;
import play.libs.Json;

public class ClassSelectionResult extends UmlResult {

  private MatchingResult<UmlClass> classResult;

  public ClassSelectionResult(UmlExercise exercise, JsonNode userJSON) {
    super(exercise);

    UmlSolution learnerSolution = Json.fromJson(userJSON, UmlSolution.class);
    classResult = CLASS_MATCHER.match(learnerSolution.getClasses(), exercise.getSolution().getClasses());
  }

  public MatchingResult<UmlClass> getClassResult() {
    return classResult;
  }

}
