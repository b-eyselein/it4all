package controllers.sql;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import model.ScriptRunner;
import model.exercise.SqlExercise;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.SqlExercise.SqlExerciseKey;
import model.exercise.SqlSampleSolution;
import model.exercise.SqlSampleSolution.SqlSampleSolutionKey;
import model.exercise.SqlScenario;
import play.Logger;
import play.db.Database;
import play.db.NamedDatabase;
import play.libs.Json;

public class SqlStartUpChecker {

  private static Logger.ALogger theLogger = Logger.of("startup");
  
  private static final String SCENARIO_FOLDER = "modules/sql/conf/resources";
  
  private Database sql_main;
  
  @Inject
  public SqlStartUpChecker(@NamedDatabase("sqlmain") Database db) {
    sql_main = db;
    performStartUpCheck();
  }
  
  private void createOrUpdateScenario(SqlScenario scenario, String shortName, String longName, String scriptFile) {
    scenario.shortName = shortName;
    scenario.longName = longName;
    scenario.scriptFile = scriptFile;
    scenario.save();
  }
  
  private void handleExercise(SqlScenario scenario, int exerciseId, SqlExType exerciseType, JsonNode exerciseNode) {
    SqlExerciseKey exerciseKey = new SqlExerciseKey(scenario.shortName, exerciseId);
    SqlExercise exercise = SqlExercise.finder.byId(exerciseKey);
    
    String text = exerciseNode.get("text").asText();
    
    if(exercise == null) {
      // Create new Exercise in DB
      exercise = new SqlExercise(exerciseKey, text, exerciseType);
      exercise.save();
    } else {
      // TODO: Update exercise
    }
    exercise.text = text;
    
    // Sample solutions!
    JsonNode sampleSolutionsNode = exerciseNode.get("sampleSolutions");
    
    if(sampleSolutionsNode == null)
      throw new IllegalArgumentException(
          "The exercise " + exercise.key.id + " in scenario " + scenario + " does not have sample solutions!");
    
    for(final Iterator<String> solutionFieldIter = sampleSolutionsNode.fieldNames(); solutionFieldIter.hasNext();) {
      String solutionId = solutionFieldIter.next();
      JsonNode sampleSolution = sampleSolutionsNode.get(solutionId);
      handleSampleSolution(solutionId, exercise, sampleSolution.asText());
    }
  }
  
  private void handleSampleSolution(String solutionId, SqlExercise exercise, String sampleSolutionText) {
    
    int id = Integer.parseInt(solutionId);
    SqlSampleSolutionKey sampleKey = new SqlSampleSolutionKey(id, exercise.key.id, exercise.key.scenarioName);
    
    SqlSampleSolution sampleSolution = SqlSampleSolution.finder.byId(sampleKey);
    if(sampleSolution == null) {
      sampleSolution = new SqlSampleSolution(sampleKey, sampleSolutionText);
      sampleSolution.save();
    } else {
      // TODO: Update sample solution!?!
    }
  }
  
  private void handleScenario(Path path) {
    String jsonAsString = "";
    
    try {
      jsonAsString = String.join("\n", Files.readAllLines(path));
    } catch (IOException e) {
      theLogger.error("Error while reading file: " + path.toString(), e);
      return;
    }
    
    if(jsonAsString.isEmpty())
      return;
    
    JsonNode json = Json.parse(jsonAsString);
    
    String shortName = json.get("shortName").asText();
    String longName = json.get("longName").asText();
    String scriptFile = json.get("scriptFile").asText();
    
    SqlScenario scenario = SqlScenario.finder.byId(shortName);
    if(scenario == null)
      scenario = new SqlScenario();
    createOrUpdateScenario(scenario, shortName, longName, scriptFile);
    
    JsonNode exercises = json.get("exercises");
    for(final Iterator<String> exerciseTypesIter = exercises.fieldNames(); exerciseTypesIter.hasNext();) {
      String exerciseTypeAsString = exerciseTypesIter.next();
      JsonNode exercisesForType = exercises.get(exerciseTypeAsString);
      SqlExType type = SqlExType.getByName(exerciseTypeAsString);
      
      for(final Iterator<String> exerciseFieldIter = exercisesForType.fieldNames(); exerciseFieldIter.hasNext();) {
        String exerciseIdAsString = exerciseFieldIter.next();
        int exerciseId = Integer.parseInt(exerciseIdAsString);
        JsonNode exerciseNode = exercisesForType.get(exerciseIdAsString);
        
        handleExercise(scenario, exerciseId, type, exerciseNode);
      }
    }
    
    // FIXME: run script to create database
    Path scriptFilePath = Paths.get(SCENARIO_FOLDER, scenario.scriptFile);
    if(Files.exists(scriptFilePath)) {
      try {
        Connection connection = sql_main.getConnection();
        connection.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + scenario.shortName);
        connection.setCatalog(scenario.shortName);
        List<String> line = Files.readAllLines(scriptFilePath);
        ScriptRunner.runScript(connection, line, false, true);
      } catch (IOException | SQLException e) {
        theLogger.error("Error while executing script file " + scriptFilePath.toString(), e);
      }
    }
  }
  
  private void performStartUpCheck() {
    Path scenarioDir = Paths.get(SCENARIO_FOLDER);
    
    if(!Files.isDirectory(scenarioDir))
      throw new RuntimeException("Path " + SCENARIO_FOLDER + " should be a directory!");
    
    try {
      DirectoryStream<Path> directoryStream = Files.newDirectoryStream(scenarioDir);
      directoryStream.forEach(file -> {
        if(file.getFileName().toString().endsWith("json")) {
          handleScenario(file);
        }
      });
    } catch (IOException e) {
      theLogger.error("Failure: ", e);
    }
  }
  
}
