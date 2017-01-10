package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.JsExercise.JsDataType;
import model.exercisereading.ExerciseReader;
import model.programming.TestDataKey;

public class JsExerciseReader extends ExerciseReader<JsExercise> {

  public JsExerciseReader() {
    super("js");
  }

  private JsTestData readTest(int exerciseId, JsonNode testNode) {
    int testId = testNode.get("id").asInt();
    TestDataKey key = new TestDataKey(exerciseId, testId);
    JsTestData test = JsTestData.finder.byId(key);

    if(test == null)
      test = new JsTestData(key);

    test.inputs = testNode.get("input").asText();
    test.output = testNode.get("output").asText();
    return test;
  }

  private List<JsTestData> readTests(int exerciseId, JsonNode testsNode) {
    List<JsTestData> tests = new LinkedList<>();

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

    exercise.title = exerciseNode.get("title").asText();
    exercise.text = exerciseNode.get("text").asText();
    exercise.declaration = exerciseNode.get("declaration").asText();
    exercise.functionname = exerciseNode.get("functionname").asText();
    exercise.sampleSolution = exerciseNode.get("samplesolution").asText();
    exercise.inputtypes = exerciseNode.get("inputtypes").asText();
    exercise.inputcount = exerciseNode.get("inputcount").asInt();
    exercise.returntype = JsDataType.valueOf(exerciseNode.get("returntype").asText());
    exercise.functionTests = readTests(exercise.id, exerciseNode.get("tests"));

    return exercise;
  }

}
