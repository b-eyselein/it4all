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

  @Override
  public void saveRead(ProgExercise exercise) {
    exercise.save();
    exercise.sampleTestData.forEach(SampleTestData::save);
  }

  private List<SampleTestData> readTests(JsonNode testsNode) {
    return StreamSupport.stream(testsNode.spliterator(), true).map(ProgExerciseReader::readTest)
        .collect(Collectors.toList());
  }

  @Override
  protected ProgExercise read(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    String title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    String author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    String text = exerciseNode.get(StringConsts.TEXT_NAME).asText();

    String functionName = exerciseNode.get("functionName").asText();
    int inputCount = exerciseNode.get("inputCount").asInt();

    String pythonSample = exerciseNode.get("pythonSample").asText();
    String jsSample = exerciseNode.get("jsSample").asText();
    String javaSample = exerciseNode.get("javaSample").asText();

    List<SampleTestData> sampleTestData = readTests(exerciseNode.get("sampleTestData"));
    
    ProgExercise exercise = ProgExercise.finder.byId(id);
    if(exercise == null)
      return new ProgExercise(id, title, author, text, functionName, inputCount, pythonSample, jsSample, javaSample);
    else
      return exercise.updateValues(id, title, author, text, functionName, inputCount, pythonSample, jsSample,
          javaSample);
  }

}
