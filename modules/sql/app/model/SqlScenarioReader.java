package model;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.ibatis.jdbc.ScriptRunner;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import model.exercisereading.ExerciseCollectionReader;
import play.Logger;
import play.data.DynamicForm;
import play.db.Database;

public class SqlScenarioReader extends ExerciseCollectionReader<SqlExercise, SqlScenario> {
  
  private static final String CREATE_DUMMY = "CREATE DATABASE IF NOT EXISTS ";
  
  private final Database sqlSelect;
  private final Database sqlOther;
  
  public SqlScenarioReader(Database theSqlSelect, Database theSqlOther) {
    super("sql", SqlScenario.finder, SqlScenario[].class);
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }
  
  private static void createDatabase(String databaseName, Connection connection) {
    try(Statement createStatement = connection.createStatement()) {
      createStatement.executeUpdate(CREATE_DUMMY + databaseName); // NOSONAR
      connection.setCatalog(databaseName);
    } catch (final SQLException e) {
      Logger.error("There has been an error running an sql script: \"" + CREATE_DUMMY + databaseName + "\"", e);
    }
  }
  
  public void initRemainingExFromForm(SqlScenario exercise, DynamicForm form) {
    exercise.setShortName(form.get(StringConsts.SHORTNAME_NAME));
    exercise.setScriptFile(form.get(StringConsts.SCRIPTFILE_NAME));
  }
  
  @Override
  public SqlScenario instantiateExercise(int id) {
    return new SqlScenario(id);
  }
  
  public void runCreateScript(Database database, SqlScenario scenario) {
    final Path scriptFilePath = Paths.get("conf", "resources", exerciseType(), scenario.getScriptFile())
        .toAbsolutePath();
    
    if(!scriptFilePath.toFile().exists()) {
      Logger.error("File " + scriptFilePath + " does not exist");
      return;
    }
    
    try {
      final Connection connection = database.getConnection();
      
      createDatabase(scenario.getShortName(), connection);
      
      final ScriptRunner runner = new ScriptRunner(connection);
      runner.setLogWriter(null);
      runner.runScript(new FileReader(scriptFilePath.toFile()));
      
      connection.close();
    } catch (IOException | SQLException e) {
      Logger.error("Error while executing script file " + scriptFilePath.toString(), e);
    }
    
  }
  
  @Override
  public void save(SqlScenario scenario) {
    scenario.save();
    
    runCreateScript(sqlSelect, scenario);
    runCreateScript(sqlOther, scenario);
    
    // scenario.getExercises().forEach(delegateReader::save);
  }
  
  protected void updateExercise(SqlScenario exercise, JsonNode exerciseNode) {
    exercise.setShortName(exerciseNode.get(StringConsts.SHORTNAME_NAME).asText());
    exercise.setScriptFile(exerciseNode.get(StringConsts.SCRIPTFILE_NAME).asText());
    
    // JsonNode exesNode = exerciseNode.get(StringConsts.EXERCISES_NAME);
    // List<SqlExercise> exercises = readArray(exesNode,
    // delegateReader::readExercise);
  }
  
}
