package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseType;
import model.exercise.SqlSample;
import model.exercise.SqlSampleKey;
import model.exercise.SqlScenario;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.db.Database;
import play.libs.Json;

public class SqlExerciseReader extends ExerciseReader<SqlScenario> {
  
  private static final String SCRIPT_FILE = "scriptFile";
  private static final String SHORT_NAME = "shortName";
  private static final String EXERCISES_NAME = "exercises";
  private static final String SOLUTION_NAME = "solution";
  
  private static final String CREATE_DUMMY = "CREATE DATABASE IF NOT EXISTS ?";
  
  public SqlExerciseReader() {
    super("sql");
  }
  
  private static void createDatabase(String databaseName, Connection connection) {
    try(Statement createStatement = connection.createStatement()) {
      createStatement.executeUpdate(CREATE_DUMMY + databaseName);
    } catch (SQLException e) {
      Logger.error("There has been an error running an sql script: \"" + CREATE_DUMMY + databaseName + "\"", e);
    }
  }
  
  private static void flushPrivileges(Connection connection) {
    try(Statement flushPrivileges = connection.createStatement();) {
      flushPrivileges.executeUpdate("FLUSH PRIVILEGES");
      flushPrivileges.close();
    } catch (SQLException e) {
      
    }
  }
  
  // FIXME: genauere Fehlermeldungen (auch auf Konsole --> Logger!)
  // BEISPIEL: Aufgabe nicht erstellt, weil TEXT_NAME oder "sampleSolutions"
  // fehlt/falsch
  
  private static void grantRights(String databaseName, Connection connection) throws SQLException {
    try(Statement grantStatement = connection.createStatement()) {
      grantStatement.executeUpdate(
          "GRANT ALL PRIVILEGES ON " + databaseName + ".* TO " + "'it4all'@localhost IDENTIFIED BY 'c4aK3?bV';");
    } catch (SQLException e) {
      Logger.error("There has been an error running an sql script: \"" + CREATE_DUMMY + databaseName + "\"", e);
    }
  }
  
  private static SqlExercise readExercise(SqlScenario scenario, JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();
    
    SqlExercise exercise = SqlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new SqlExercise(id);
    
    exercise.scenario = scenario;
    exercise.author = "admin";
    exercise.exerciseType = SqlExerciseType.valueOf(exerciseNode.get("exerciseType").asText());
    
    exercise.text = exerciseNode.get(StringConsts.TEXT_NAME).asText();
    exercise.samples = readSampleSolutions(exerciseNode.get("sampleSolutions"));
    
    exercise.validation = exerciseNode.get("validation").asText();
    exercise.hint = exerciseNode.get("hint").asText();
    
    exercise.tags = readTags(exerciseNode.get("tags"));
    
    return exercise;
  }
  
  private static List<SqlExercise> readExercises(SqlScenario scenario, JsonNode exerciseNodes) {
    final List<SqlExercise> exercises = new LinkedList<>();
    
    for(final Iterator<JsonNode> exercisesIter = exerciseNodes.elements(); exercisesIter.hasNext();)
      exercises.add(readExercise(scenario, exercisesIter.next()));
    
    return exercises;
  }
  
  private static SqlSample readSampleSolution(JsonNode sampleSolNode) {
    SqlSampleKey key = Json.fromJson(sampleSolNode.get(StringConsts.KEY_NAME), SqlSampleKey.class);
    
    SqlSample sample = SqlSample.finder.byId(key);
    if(sample == null)
      sample = new SqlSample(key);
    
    sample.sample = String.join("\n", JsonWrapper.parseJsonArrayNode(sampleSolNode.get(SOLUTION_NAME)));
    return sample;
  }
  
  private static List<SqlSample> readSampleSolutions(JsonNode sampleSolutions) {
    List<SqlSample> samples = new LinkedList<>();
    
    for(Iterator<JsonNode> sampleSolIter = sampleSolutions.iterator(); sampleSolIter.hasNext();)
      samples.add(readSampleSolution(sampleSolIter.next()));
    return samples;
  }
  
  private static String readTags(JsonNode tagsNode) {
    return String.join(SqlExercise.SAMPLE_JOIN_CHAR, JsonWrapper.parseJsonArrayNode(tagsNode));
  }
  
  // FIXME: Impelement, test and use!
  public void runCreateScript(Database database, SqlScenario scenario) {
    Path scriptFilePath = Paths.get(BASE_DIR, "sql", scenario.scriptFile);
    
    if(!scriptFilePath.toFile().exists())
      // TODO: return / throw error?
      return;
    
    try {
      Logger.info("Running script " + scriptFilePath);
      Connection connection = database.getConnection();
      
      // Create database and grant rights to user
      createDatabase(scenario.shortName, connection);
      
      // Grant rights to user and flush privileges
      grantRights(scenario.shortName, connection);
      
      flushPrivileges(connection);
      
      connection.setCatalog(scenario.shortName);
      
      List<String> line = Files.readAllLines(scriptFilePath);
      ScriptRunner.runScript(connection, line, false, true);
      connection.close();
    } catch (IOException | SQLException e) {
      READING_LOGGER.error("Error while executing script file " + scriptFilePath.toString(), e);
    }
    
  }
  
  @Override
  protected SqlScenario readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();
    
    SqlScenario scenario = SqlScenario.finder.byId(id);
    if(scenario == null)
      scenario = new SqlScenario(id);
    
    scenario.shortName = exerciseNode.get(SHORT_NAME).asText();
    scenario.author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    scenario.title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    scenario.text = exerciseNode.get(StringConsts.TEXT_NAME).asText();
    scenario.scriptFile = exerciseNode.get(SCRIPT_FILE).asText();
    scenario.exercises = readExercises(scenario, exerciseNode.get(EXERCISES_NAME));
    
    return scenario;
  }
  
}
