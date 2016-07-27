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
        JsonNode exerciseNode = exerciseIter.next();

        // FIXME: check, ob entsprechende Nodes vorhanden!?!
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

        // FIXME: Sample solutions!
        JsonNode sampleSolutionsNode = exerciseNode.get("sampleSolutions");
        for(final Iterator<JsonNode> solutionsIter = sampleSolutionsNode.elements(); solutionsIter.hasNext();) {
          JsonNode sampleSolutionNode = solutionsIter.next();
          int id = sampleSolutionNode.get("id").asInt();
          String sample = sampleSolutionNode.get("sample").asText();

          SqlSampleSolutionKey sampleKey = new SqlSampleSolutionKey(id, exercise.key.id, scenarioName);

          SqlSampleSolution sampleSolution = SqlSampleSolution.finder.byId(sampleKey);
          if(sampleSolution == null) {
            sampleSolution = new SqlSampleSolution(sampleKey, sample);
            sampleSolution.save();
          } else {
            // TODO: Update sample solution!?!
          }
        }

      }
    }
  }

  private static void performStartUpCheck() {
    String folder = "modules/sql/conf/resources/scenarioes";
    Path scenarioDir = Paths.get(folder);

    if(!Files.isDirectory(scenarioDir))
      throw new RuntimeException("Path " + folder + " should be a directory!");

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
