package model.result;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlExercise;
import model.UmlSolution;
import model.matcher.ClassMatchingResult;
import model.matching.StringMatchingResult;
import play.libs.Json;

public class ClassSelectionResult extends UmlResult {

  private ClassMatchingResult classResult;
  
  private StringMatchingResult attributesResult;
  private StringMatchingResult methodsResult;
  
  public ClassSelectionResult(UmlExercise exercise, JsonNode userJSON) {
    super(exercise);
    
    UmlSolution learnerSolution = Json.fromJson(userJSON, UmlSolution.class);
    UmlSolution musterSolution = exercise.getSolution();
    
    classResult = CLASS_MATCHER.match(learnerSolution.getClasses(), musterSolution.getClasses());
    
    attributesResult = STRING_MATCHER.match(learnerSolution.getOtherAttributes(), musterSolution.getOtherAttributes());
    methodsResult = STRING_MATCHER.match(learnerSolution.getOtherMethods(), musterSolution.getOtherMethods());
  }
  
  public StringMatchingResult getAttributesResult() {
    return attributesResult;
  }
  
  public ClassMatchingResult getClassResult() {
    return classResult;
  }
  
  public StringMatchingResult getMethodsResult() {
    return methodsResult;
  }
  
}
