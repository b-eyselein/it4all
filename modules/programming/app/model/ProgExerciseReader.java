package model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.testdata.SampleTestData;
import model.testdata.SampleTestDataKey;
import play.libs.Json;

public class ProgExerciseReader extends ExerciseReader<ProgExercise> {

  private static final ProgExerciseReader INSTANCE = new ProgExerciseReader();

  private ProgExerciseReader() {
    super("prog", ProgExercise.finder, ProgExercise[].class);
  }

  public static ProgExerciseReader getInstance() {
    return INSTANCE;
  }

  public static ProgSample readSample(JsonNode sampleNode) {
    ProgSampleKey key = Json.fromJson(sampleNode.get(StringConsts.KEY_NAME), ProgSampleKey.class);
    String sampleString = sampleNode.get(StringConsts.SAMPLE_NAME).asText();

    ProgSample sample = ProgSample.finder.byId(key);
    if(sample == null)
      return new ProgSample(key, sampleString);
    else
      return sample.updateValues(key, sampleString);
  }

  public static SampleTestData readTest(JsonNode testNode) {
    SampleTestDataKey key = Json.fromJson(testNode.get(StringConsts.KEY_NAME), SampleTestDataKey.class);

    SampleTestData test = SampleTestData.finder.byId(key);
    if(test == null)
      test = new SampleTestData(key);

    test.inputs = testNode.get(StringConsts.INPUTS_NAME).asText();
    test.output = testNode.get("output").asText();
    return test;
  }

  @Override
  public void saveRead(ProgExercise exercise) {
    exercise.save();
    exercise.sampleTestData.forEach(SampleTestData::save);
  }

  @Override
  protected ProgExercise instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    String functionName = exerciseNode.get("functionName").asText();
    int inputCount = exerciseNode.get("inputCount").asInt();

    List<ProgSample> samples = readArray(exerciseNode.get(StringConsts.SAMPLES_NAME), ProgExerciseReader::readSample);
    List<SampleTestData> sampleTestData = readArray(exerciseNode.get("sampleTestData"), ProgExerciseReader::readTest);

    return new ProgExercise(id, title, author, text, functionName, inputCount, samples, sampleTestData);
  }

}
