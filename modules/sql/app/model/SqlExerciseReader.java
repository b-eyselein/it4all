package model;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseType;
import model.exercise.SqlSample;
import model.exercise.SqlSampleKey;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;

public class SqlExerciseReader extends ExerciseReader<SqlExercise> {

  private static final SqlExerciseReader INSTANCE = new SqlExerciseReader();

  public SqlExerciseReader() {
    super("sql", SqlExercise.finder, SqlExercise[].class);
  }

  public static ExerciseReader<SqlExercise> getInstance() {
    return INSTANCE;
  }

  public static SqlSample readSampleSolution(JsonNode sampleSolNode) {
    final SqlSampleKey key = Json.fromJson(sampleSolNode.get(StringConsts.KEY_NAME), SqlSampleKey.class);

    SqlSample sample = SqlSample.finder.byId(key);
    if(sample == null)
      sample = new SqlSample(key);

    sample.sample = readTextArray(sampleSolNode.get("sample"), "\n");

    Logger.debug(sample.sample);

    return sample;
  }

  @Override
  public void initRemainingExFromForm(SqlExercise exercise, DynamicForm form) {
    // TODO Auto-generated method stub

    exercise.exerciseType = SqlExerciseType.valueOf(form.get(StringConsts.EXERCISE_TYPE));

    // exercise.samples = readArray(form.get(StringConsts.SAMPLES_NAME),
    // SqlExerciseReader::readSampleSolution);
    exercise.hint = form.get("hint");
    // exercise.tags = String.join(SqlExercise.SAMPLE_JOIN_CHAR,
    // parseJsonArrayNode(form.get("tags")));
  }

  @Override
  public SqlExercise instantiateExercise(int id) {
    return new SqlExercise(id);
  }

  @Override
  public void saveExercise(SqlExercise exercise) {
    exercise.save();
    exercise.samples.forEach(SqlSample::save);
  }

  @Override
  protected void updateExercise(SqlExercise exercise, JsonNode exerciseNode) {
    exercise.exerciseType = SqlExerciseType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText());

    exercise.samples = readArray(exerciseNode.get(StringConsts.SAMPLES_NAME), SqlExerciseReader::readSampleSolution);
    exercise.hint = exerciseNode.get("hint").asText();
    exercise.tags = String.join(SqlExercise.SAMPLE_JOIN_CHAR, parseJsonArrayNode(exerciseNode.get("tags")));
  }

}
