package controllers.sql;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercise.SqlExercise;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.SqlExercise.SqlExerciseKey;
import model.exercise.SqlSampleSolution;
import model.exercise.SqlSampleSolution.SqlSampleSolutionKey;
import model.exercise.SqlScenario;
import play.Logger;
import play.libs.Json;

public class SqlStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  
  private static final String SCENARIO_FOLDER = "modules/sql/conf/resources/scenarioes";

  private static void handleExercise(String scenarioName, String exerciseType, JsonNode exerciseNode) {
    SqlExerciseKey exerciseKey = new SqlExerciseKey(scenarioName, exerciseNode.get("id").asInt());
    String title = exerciseNode.get("title").asText();
    String text = exerciseNode.get("text").asText();
    SqlExType type = SqlExType.getByName(exerciseType);
    
    SqlExercise exercise = SqlExercise.finder.byId(exerciseKey);
    if(exercise == null) {
      // Create new Exercise in DB
      exercise = new SqlExercise(exerciseKey, title, text, type);
      exercise.save();
    } else {
      // TODO: Update exercise
    }
    
    // Sample solutions!
    JsonNode sampleSolutionsNode = exerciseNode.get("sampleSolutions");

    if(sampleSolutionsNode == null || !sampleSolutionsNode.isArray())
      throw new IllegalArgumentException("The exercise " + exercise.key.id + " in scenario " + scenarioName
          + " does not have sample solutions or the solutions are not correctly specified as an array!");
    
    for(final Iterator<JsonNode> solutionsIter = sampleSolutionsNode.elements(); solutionsIter.hasNext();) {
      handleSampleSolution(scenarioName, exercise, solutionsIter.next());
    }
  }
  
  private static void handleSampleSolution(String scenarioName, SqlExercise exercise, JsonNode sampleSolutionNode) {
    JsonNode idNode = sampleSolutionNode.get("id"), sampleNode = sampleSolutionNode.get("sample");
    
    if(idNode == null || sampleNode == null)
      throw new IllegalArgumentException("The id or sample of a sample solution for exercise " + exercise.key.id
          + " in scenario " + scenarioName + " is missing!");
    
    if(!idNode.canConvertToInt())
      throw new IllegalArgumentException("Node for id " + idNode.asText() + " should be an int!");
    
    SqlSampleSolutionKey sampleKey = new SqlSampleSolutionKey(idNode.asInt(), exercise.key.id, scenarioName);
    
    SqlSampleSolution sampleSolution = SqlSampleSolution.finder.byId(sampleKey);
    if(sampleSolution == null) {
      sampleSolution = new SqlSampleSolution(sampleKey, sampleNode.asText());
      sampleSolution.save();
    } else {
      // TODO: Update sample solution!?!
    }
  }

  private static void handleScenario(Path path) {
    String jsonAsString = "";
    
    try {
      jsonAsString = String.join("\n", Files.readAllLines(path));
    } catch (IOException e) {
      theLogger.error("Error while reading file: " + path.toString(), e);
    }
    
    if(jsonAsString.isEmpty())
      return;
    
    JsonNode json = Json.parse(jsonAsString);
    
    String scenarioName = json.get("shortName").asText();
    SqlScenario scenario = SqlScenario.finder.byId(scenarioName);
    
    if(scenario == null) {
      // Create SqlScenario in DB
      scenario = new SqlScenario(scenarioName, json.get("longName").asText());
      scenario.save();
    }
    
    JsonNode exercises = json.get("exercises");
    for(final Iterator<String> typeIter = exercises.fieldNames(); typeIter.hasNext();) {
      String exerciseType = typeIter.next();
      JsonNode exercisesForType = exercises.get(exerciseType);
      
      for(final Iterator<JsonNode> exerciseIter = exercisesForType.elements(); exerciseIter.hasNext();) {
        handleExercise(scenarioName, exerciseType, exerciseIter.next());
        
      }
    }
  }
  
  private static void performStartUpCheck() {
    Path scenarioDir = Paths.get(SCENARIO_FOLDER);
    
    if(!Files.isDirectory(scenarioDir))
      throw new RuntimeException("Path " + SCENARIO_FOLDER + " should be a directory!");
    
    try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(scenarioDir)) {
      for(final Iterator<Path> it = directoryStream.iterator(); it.hasNext();)
        handleScenario(it.next());
    } catch (NullPointerException | IOException e) {
      theLogger.error("Failure: ", e);
    } catch (PersistenceException e) {
      theLogger.error("Failure: ", e);
    }
  }
  
  public SqlStartUpChecker() {
    performStartUpCheck();
  }
  
}
