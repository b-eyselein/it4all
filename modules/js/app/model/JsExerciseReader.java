package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.JsExercise.JsDataType;
import model.exercisereading.ExerciseReader;

public class JsExerciseReader extends ExerciseReader<JsExercise> {
  
  private JsTest readTest(int exerciseId, JsonNode testNode) {
    int testId = testNode.get("id").asInt();
    JsTestKey key = new JsTestKey(exerciseId, testId);
    JsTest test = JsTest.finder.byId(key);
    
    if(test == null)
      test = new JsTest(key);
    
    test.inputs = testNode.get("input").asText();
    test.output = testNode.get("output").asText();
    return test;
  }
  
  private List<JsTest> readTests(int exerciseId, JsonNode testsNode) {
    List<JsTest> tests = new LinkedList<>();
    
    for(final Iterator<JsonNode> testIter = testsNode.elements(); testIter.hasNext();)
      tests.add(readTest(exerciseId, testIter.next()));
    
    return tests;
  }
  
  @Override
  protected JsExercise readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get("id").asInt();
    JsExercise exercise = JsExercise.finder.byId(id);
    
    if(exercise == null)
      exercise = new JsExercise(id);
    
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode textNode = exerciseNode.get("text");
    JsonNode declarationNode = exerciseNode.get("declaration");
    
    exercise.title = titleNode.asText();
    exercise.text = textNode.asText();
    exercise.declaration = declarationNode.asText();
    exercise.functionname = exerciseNode.get("functionname").asText();
    exercise.sampleSolution = exerciseNode.get("samplesolution").asText();
    exercise.inputtypes = exerciseNode.get("inputtypes").asText();
    exercise.inputcount = exerciseNode.get("inputcount").asInt();
    exercise.returntype = JsDataType.valueOf(exerciseNode.get("returntype").asText());
    exercise.functionTests = readTests(exercise.getId(), exerciseNode.get("tests"));
    
    return exercise;
  }
  
}
