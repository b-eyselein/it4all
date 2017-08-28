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
import model.exercise.SqlScenario;
import model.exercisereading.ExerciseCollectionReader;
import play.Logger;
import play.db.Database;

public class SqlScenarioReader extends ExerciseCollectionReader<SqlExercise, SqlScenario> {

  private static final String CREATE_DUMMY = "CREATE DATABASE IF NOT EXISTS ";

  private Database sqlSelect;
  private Database sqlOther;

  public SqlScenarioReader(Database theSqlSelect, Database theSqlOther) {
    super(SqlExerciseReader.getInstance(), SqlScenario.finder, SqlScenario[].class);
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

  public void runCreateScript(Database database, SqlScenario scenario) {
    Path scriptFilePath = Paths.get("conf", "resources", exerciseType, scenario.getScriptFile()).toAbsolutePath();

    if(!scriptFilePath.toFile().exists()) {
      Logger.error("File " + scriptFilePath + " does not exist");
      return;
    }

    try {
      Connection connection = database.getConnection();

      createDatabase(scenario.getShortName(), connection);

      ScriptRunner runner = new ScriptRunner(connection);
      runner.setLogWriter(null);
      runner.runScript(new FileReader(scriptFilePath.toFile()));

      connection.close();
    } catch (IOException | SQLException e) {
      Logger.error("Error while executing script file " + scriptFilePath.toString(), e);
    }

  }

  @Override
  public void saveRead(SqlScenario scenario) {
    scenario.save();

    runCreateScript(sqlSelect, scenario);
    runCreateScript(sqlOther, scenario);

    scenario.getExercises().forEach(delegateReader::saveRead);
  }

  @Override
  protected SqlScenario instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    String shortName = exerciseNode.get(StringConsts.SHORTNAME_NAME).asText();
    String scriptFile = exerciseNode.get(StringConsts.SCRIPTFILE_NAME).asText();
    
    JsonNode exesNode = exerciseNode.get(StringConsts.EXERCISES_NAME);
    List<SqlExercise> exercises = readArray(exesNode, delegateReader::readExercise);

    return new SqlScenario(id, title, author, text, shortName, scriptFile, exercises);
  }
  
}
