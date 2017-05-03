package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.testdata.SampleTestData;
import model.testdata.SampleTestDataKey;
import play.Logger;
import play.libs.Json;

public class ProgExerciseReader extends ExerciseReader<ProgExercise> {

  public ProgExerciseReader() {
    super("prog");
  }

  private SampleTestData readTest(JsonNode testNode) {
    SampleTestDataKey key = Json.fromJson(testNode.get(KEY_NAME), SampleTestDataKey.class);
    SampleTestData test = SampleTestData.finder.byId(key);

    if(test == null)
      test = new SampleTestData(key);

    test.inputs = testNode.get("input").asText();
    test.output = testNode.get("output").asText();
    return test;
  }

  private List<SampleTestData> readTests(JsonNode testsNode) {
    List<SampleTestData> tests = new LinkedList<>();

    testsNode.elements().forEachRemaining(testNode -> tests.add(readTest(testNode)));

    return tests;
  }

  @Override
  protected ProgExercise readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get(ID_NAME).asInt();
    ProgExercise exercise = ProgExercise.finder.byId(id);

    if(exercise == null)
      exercise = new ProgExercise(id);

    exercise.title = exerciseNode.get(TITLE_NAME).asText();
    exercise.text = exerciseNode.get(TEXT_NAME).asText();

    exercise.functionName = exerciseNode.get("functionName").asText();
    exercise.inputCount = exerciseNode.get("inputCount").asInt();

    exercise.pythonSample = exerciseNode.get("pythonSample").asText();
    exercise.jsSample = exerciseNode.get("jsSample").asText();

    exercise.sampleTestData = readTests(exerciseNode.get("sampleTestData"));

    return exercise;
  }

}
