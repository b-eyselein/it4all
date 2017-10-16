package model;

import java.util.Collections;

import com.fasterxml.jackson.databind.JsonNode;
import model.exercisereading.ExerciseReader;
import model.testdata.SampleTestData;
import model.testdata.SampleTestDataKey;
import play.data.DynamicForm;
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
    final ProgSampleKey key = Json.fromJson(sampleNode.get(StringConsts$.MODULE$.KEY_NAME()), ProgSampleKey.class);
    final String sampleString = sampleNode.get(StringConsts$.MODULE$.SAMPLE_NAME()).asText();

    final ProgSample sample = ProgSample.finder.byId(key);
    if(sample == null)
      return new ProgSample(key, sampleString);
    else
      return sample.updateValues(key, sampleString);
  }

  public static SampleTestData readTest(JsonNode testNode) {
    final SampleTestDataKey key = Json
        .fromJson(testNode.get(StringConsts$.MODULE$.KEY_NAME()), SampleTestDataKey.class);

    SampleTestData test = SampleTestData.finder.byId(key);
    if(test == null)
      test = new SampleTestData(key);

    test.inputs = testNode.get(StringConsts$.MODULE$.INPUTS_NAME()).asText();
    test.output = testNode.get("output").asText();
    return test;
  }

  @Override public void initRemainingExFromForm(ProgExercise exercise, DynamicForm form) {
    exercise.setFunctionName(form.get("functionName"));
    exercise.setInputCount(Integer.parseInt(form.get("inputCount")));

    exercise.setSamples(Collections.emptyList());
    exercise.setSampleTestData(Collections.emptyList());
  }

  @Override public ProgExercise instantiate(int id) {
    return new ProgExercise(id);
  }

  @Override public void save(ProgExercise exercise) {
    exercise.save();
    exercise.sampleTestData.forEach(SampleTestData::save);
  }

  @Override public void updateExercise(ProgExercise exercise, JsonNode exerciseNode) {
    exercise.setFunctionName(exerciseNode.get("functionName").asText());
    exercise.setInputCount(exerciseNode.get("inputCount").asInt());

    // exercise.setSamples(
    // ExerciseReader.readArray(exerciseNode.get(StringConsts$.MODULE$.SAMPLES_NAME()),
    // ProgExerciseReader::readSample));
    // exercise
    // .setSampleTestData(ExerciseReader.readArray(exerciseNode.get("sampleTestData"),
    // ProgExerciseReader::readTest));
  }

}
