package controllers.sql;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercise.SqlExercise;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.SqlExercise.SqlExerciseKey;
import model.exercise.SqlScenario;
import play.Logger;
import play.libs.Json;

public class SqlStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  
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
    for(final Iterator<JsonNode> exerciseIter = exercises.elements(); exerciseIter.hasNext();) {
      JsonNode exerciseNode = exerciseIter.next();
      
      SqlExerciseKey exerciseKey = new SqlExerciseKey(scenarioName, exerciseNode.get("id").asInt());
      String title = exerciseNode.get("title").asText();
      String text = exerciseNode.get("text").asText();
      SqlExType type = SqlExType.getByName(exerciseNode.get("type").asText());
      
      SqlExercise exercise = SqlExercise.finder.byId(exerciseKey);
      if(exercise == null) {
        // Create new Exercise in DB
        exercise = new SqlExercise(exerciseKey, title, text, type);
        exercise.save();
      } else {
        // TODO: Update exercise
      }
    }
  }
  
  private static void performStartUpCheck() {
    String folder = "modules/sql/conf/resources/scenarioes";
    Path scenarioDir = Paths.get(folder);
    
    if(!Files.isDirectory(scenarioDir))
      throw new RuntimeException("Path " + folder + " should be a directory!");
    
    try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(scenarioDir)) {
      for(final Iterator<Path> it = directoryStream.iterator(); it.hasNext();) {
        handleScenario(it.next());
      }
    } catch (IOException ex) {
    }
    
  }
  
  public SqlStartUpChecker() {
    performStartUpCheck();
  }
  
}
