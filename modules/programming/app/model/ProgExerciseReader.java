package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.testdata.SampleTestData;
import model.testdata.SampleTestDataKey;
import play.libs.Json;

public class ProgExerciseReader extends ExerciseReader<ProgExercise> {

  public ProgExerciseReader() {
    super("prog");
  }

  private static SampleTestData readTest(JsonNode testNode) {
    SampleTestDataKey key = Json.fromJson(testNode.get(StringConsts.KEY_NAME), SampleTestDataKey.class);
    
    SampleTestData test = SampleTestData.finder.byId(key);
    if(test == null)
      test = new SampleTestData(key);

    test.inputs = testNode.get("input").asText();
    test.output = testNode.get("output").asText();
    return test;
  }

  private List<SampleTestData> readTests(JsonNode testsNode) {
    return StreamSupport.stream(testsNode.spliterator(), true).map(ProgExerciseReader::readTest)
        .collect(Collectors.toList());
  }

  @Override
  protected ProgExercise readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    ProgExercise exercise = ProgExercise.finder.byId(id);
    if(exercise == null)
      exercise = new ProgExercise(id);

    exercise.title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    exercise.text = exerciseNode.get(StringConsts.TEXT_NAME).asText();

    exercise.functionName = exerciseNode.get("functionName").asText();
    exercise.inputCount = exerciseNode.get("inputCount").asInt();

    exercise.pythonSample = exerciseNode.get("pythonSample").asText();
    exercise.jsSample = exerciseNode.get("jsSample").asText();

    exercise.sampleTestData = readTests(exerciseNode.get("sampleTestData"));

    return exercise;
  }

}
