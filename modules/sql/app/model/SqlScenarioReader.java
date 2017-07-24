package model;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlSample;
import model.exercise.SqlSampleKey;
import model.exercise.SqlScenario;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.db.Database;
import play.libs.Json;

public class SqlScenarioReader extends ExerciseReader<SqlScenario> {

  private static final String CREATE_DUMMY = "CREATE DATABASE IF NOT EXISTS ";

  private Database sqlSelect;
  private Database sqlOther;

  public SqlScenarioReader(Database theSqlSelect, Database theSqlOther) {
    super("sql");
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }

  private static void createDatabase(String databaseName, Connection connection) {
    try(Statement createStatement = connection.createStatement()) {
      createStatement.executeUpdate(CREATE_DUMMY + databaseName); // NOSONAR
      connection.setCatalog(databaseName);
    } catch (SQLException e) {
      Logger.error("There has been an error running an sql script: \"" + CREATE_DUMMY + databaseName + "\"", e);
    }
  }

  private static SqlSample readSampleSolution(JsonNode sampleSolNode) {
    SqlSampleKey key = Json.fromJson(sampleSolNode.get(StringConsts.KEY_NAME), SqlSampleKey.class);

    SqlSample sample = SqlSample.finder.byId(key);
    if(sample == null)
      sample = new SqlSample(key);

    sample.sample = JsonWrapper.readTextArray(sampleSolNode.get("sample"), "\n");
    return sample;
  }

  private static SqlExercise readScenarioExercise(JsonNode exerciseNode) {
    SqlExerciseKey key = Json.fromJson(exerciseNode.get(StringConsts.KEY_NAME), SqlExerciseKey.class);

    SqlExercise exercise = SqlExercise.finder.byId(key);
    if(exercise == null)
      exercise = new SqlExercise(key);

    exercise.author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    exercise.exerciseType = SqlExerciseType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText());

    exercise.text = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");
    exercise.samples = readArray(exerciseNode.get(StringConsts.SAMPLES_NAME), SqlScenarioReader::readSampleSolution);
    exercise.hint = exerciseNode.get("hint").asText();
    exercise.tags = String.join(SqlExercise.SAMPLE_JOIN_CHAR, JsonWrapper.parseJsonArrayNode(exerciseNode.get("tags")));

    return exercise;
  }

  public void runCreateScript(Database database, SqlScenario scenario) {
    Path scriptFilePath = Paths.get(baseDirForExType.toString(), scenario.scriptFile).toAbsolutePath();

    if(!scriptFilePath.toFile().exists()) {
      READING_LOGGER.error("File " + scriptFilePath + " does not exist");
      return;
    }

    try {
      Connection connection = database.getConnection();

      createDatabase(scenario.shortName, connection);

      ScriptRunner runner = new ScriptRunner(connection);
      runner.setLogWriter(null);
      runner.runScript(new FileReader(scriptFilePath.toFile()));

      connection.close();
    } catch (IOException | SQLException e) {
      READING_LOGGER.error("Error while executing script file " + scriptFilePath.toString(), e);
    }

  }

  @Override
  public void saveRead(SqlScenario scenario) {
    scenario.save();

    runCreateScript(sqlSelect, scenario);
    runCreateScript(sqlOther, scenario);

    scenario.exercises.forEach(ex -> {
      ex.save();
      ex.samples.forEach(SqlSample::save);
    });
  }

  @Override
  protected SqlScenario read(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    String author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    String title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    String text = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");

    String shortName = exerciseNode.get(StringConsts.SHORTNAME_NAME).asText();
    String scriptFile = exerciseNode.get(StringConsts.SCRIPTFILE_NAME).asText();
    List<SqlExercise> exercises = readArray(exerciseNode.get(StringConsts.EXERCISES_NAME),
        SqlScenarioReader::readScenarioExercise);

    SqlScenario scenario = SqlScenario.finder.byId(id);
    if(scenario == null)
      return new SqlScenario(id, title, author, text, shortName, scriptFile, exercises);
    else
      return scenario.updateValues(id, title, author, text, shortName, scriptFile, exercises);
  }

}
