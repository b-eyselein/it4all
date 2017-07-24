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
    super("prog");
  }
  
  public static ProgExerciseReader getInstance() {
    return INSTANCE;
  }
  
  private static ProgSample readSample(JsonNode sampleNode) {
    ProgSampleKey key = Json.fromJson(sampleNode.get(StringConsts.KEY_NAME), ProgSampleKey.class);
    String sampleString = sampleNode.get(StringConsts.SAMPLE_NAME).asText();
    
    ProgSample sample = ProgSample.finder.byId(key);
    if(sample == null)
      return new ProgSample(key, sampleString);
    else
      return sample.updateValues(key, sampleString);
  }
  
  private static SampleTestData readTest(JsonNode testNode) {
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
  protected ProgExercise read(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();
    String title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    String author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    String text = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");
    
    String functionName = exerciseNode.get("functionName").asText();
    int inputCount = exerciseNode.get("inputCount").asInt();
    
    List<ProgSample> samples = readArray(exerciseNode.get(StringConsts.SAMPLES_NAME), ProgExerciseReader::readSample);
    List<SampleTestData> sampleTestData = readArray(exerciseNode.get("sampleTestData"), ProgExerciseReader::readTest);
    
    ProgExercise exercise = ProgExercise.finder.byId(id);
    if(exercise == null)
      return new ProgExercise(id, title, author, text, functionName, inputCount, samples, sampleTestData);
    else
      return exercise.updateValues(id, title, author, text, functionName, inputCount, samples, sampleTestData);
  }
  
}
