package model.result;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlClass;
import model.UmlExercise;
import model.UmlSolution;
import model.matcher.UmlClassMatch;
import model.matching.MatchingResult;
import model.matching.StringMatch;
import play.libs.Json;

public class ClassSelectionResult extends UmlResult {
  
  private MatchingResult<UmlClass, UmlClassMatch> classResult;

  private MatchingResult<String, StringMatch> attributesResult;
  private MatchingResult<String, StringMatch> methodsResult;

  public ClassSelectionResult(UmlExercise exercise, JsonNode userJSON) {
    super(exercise);

    UmlSolution learnerSolution = Json.fromJson(userJSON, UmlSolution.class);
    UmlSolution musterSolution = exercise.getSolution();

    classResult = CLASS_MATCHER.match(learnerSolution.getClasses(), musterSolution.getClasses());

    attributesResult = STRING_MATCHER.match(learnerSolution.getOtherAttributes(), musterSolution.getOtherAttributes());
    methodsResult = STRING_MATCHER.match(learnerSolution.getOtherMethods(), musterSolution.getOtherMethods());
  }

  public MatchingResult<String, StringMatch> getAttributesResult() {
    return attributesResult;
  }

  public MatchingResult<UmlClass, UmlClassMatch> getClassResult() {
    return classResult;
  }

  public MatchingResult<String, StringMatch> getMethodsResult() {
    return methodsResult;
  }

}
