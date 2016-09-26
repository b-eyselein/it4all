package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.StartUpChecker;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import model.exercise.update.DeleteExercise;
import model.exercise.update.InsertExercise;
import model.exercise.update.UpdateExercise;
import play.Logger;
import play.db.Database;

public class SqlScenarioHandler {

  private static Logger.ALogger theLogger = Logger.of("startup");
  private static final String SCENARIO_FOLDER = "conf/resources/sql";

  public static void handleScenario(Path path, Database database) {
    JsonNode json = StartUpChecker.readJsonFile(path);

    SqlScenario scenario = readAndUpdateScenario(json);

    JsonNode exercises = json.get("exercises");
    if(exercises != null)
      handleExercises(scenario, exercises);
    else
      theLogger.debug("There were no exercises specified in file " + path);

    // FIXME: run script to create database
    runCreateScript(database, scenario);
  }

  private static void handleExercises(SqlScenario scenario, JsonNode exercises) {
    for(final Iterator<String> exerciseTypesIter = exercises.fieldNames(); exerciseTypesIter.hasNext();) {
      String exerciseTypeAsString = exerciseTypesIter.next();

      SqlExerciseType exerciseType = SqlExerciseType.valueOf(exerciseTypeAsString);
      if(exerciseType == null)
        throw new IllegalArgumentException("Exercisetype " + exerciseTypeAsString + " does not exist!");

      List<SqlExercise> exercisesForType = handleExercisesForType(scenario, exerciseType,
          exercises.get(exerciseTypeAsString));
      for(SqlExercise exercise: exercisesForType)
        exercise.save();
    }
  }

  private static List<SqlExercise> handleExercisesForType(SqlScenario scenario, SqlExerciseType exerciseType,
      JsonNode exercisesNodes) {
    List<SqlExercise> exercises = new LinkedList<>();

    for(final Iterator<String> exerciseFieldIter = exercisesNodes.fieldNames(); exerciseFieldIter.hasNext();) {
      String exerciseIdAsString = exerciseFieldIter.next();
      int exerciseId = Integer.parseInt(exerciseIdAsString);
      JsonNode exerciseNode = exercisesNodes.get(exerciseIdAsString);
      exercises.add(readExercise(scenario, exerciseId, exerciseType, exerciseNode));
    }
    return exercises;
  }

  private static SqlScenario readAndUpdateScenario(JsonNode json) {
    String shortName = json.get("shortName").asText();
    SqlScenario scenario = SqlScenario.finder.byId(shortName);
    if(scenario == null)
      scenario = new SqlScenario(shortName);

    scenario.longName = json.get("longName").asText();
    scenario.scriptFile = json.get("scriptFile").asText();
    scenario.save();
    return scenario;
  }

  private static SqlExercise readExercise(SqlScenario scenario, int exerciseId, SqlExerciseType exerciseType,
      JsonNode exerciseNode) {
    SqlExerciseKey exerciseKey = new SqlExerciseKey(scenario.shortName, exerciseId);
    SqlExercise exercise = SqlExercise.finder.byId(exerciseKey);

    if(exercise == null)
      exercise = SqlExercise.instantiate(exerciseKey, exerciseType);

    // Update text and samples olutions, key and type remain the same
    JsonNode textNode = exerciseNode.get("text");
    JsonNode sampleSolutionsNode = exerciseNode.get("sampleSolutions");

    if(textNode == null || sampleSolutionsNode == null)
      return null;

    exercise.text = textNode.asText();
    exercise.samples = readSampleSolutions(sampleSolutionsNode);

    readExtra(exerciseType, exercise, exerciseNode);

    return exercise;
  }

  private static void readExtra(SqlExerciseType exerciseType, SqlExercise exercise, JsonNode exerciseNode) {
    JsonNode validationNode = exerciseNode.get("validation");
    switch(exerciseType) {
    case CREATE:
    case SELECT:
      return;
    case DELETE:
      if(validationNode != null)
        ((DeleteExercise) exercise).validation = validationNode.asText();
      return;
    case UPDATE:
      if(validationNode != null)
        ((UpdateExercise) exercise).validation = validationNode.asText();
      return;
    case INSERT:
      if(validationNode != null)
        ((InsertExercise) exercise).validation = validationNode.asText();
      return;
    default:
      return;
    }
  }

  private static String readSampleSolutions(JsonNode sampleSolutions) {
    List<String> samples = new LinkedList<>();
    sampleSolutions.elements().forEachRemaining(el -> samples.add(el.asText()));
    return String.join(SqlExercise.SAMPLE_JOIN_CHAR, samples);
  }

  private static void runCreateScript(Database database, SqlScenario scenario) {
    Path scriptFilePath = Paths.get(SCENARIO_FOLDER, scenario.scriptFile);
    if(Files.exists(scriptFilePath)) {
      try {
        Logger.info("Running script " + scriptFilePath);
        Connection connection = database.getConnection();
        // Create database and grant rights to user
        connection.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + scenario.shortName);
        connection.createStatement().executeUpdate("GRANT ALL PRIVILEGES ON " + scenario.shortName + ".* TO "
            + "'it4all'@localhost IDENTIFIED BY 'c4aK3?bV';");
        connection.createStatement().executeUpdate("FLUSH PRIVILEGES");
        connection.setCatalog(scenario.shortName);

        List<String> line = Files.readAllLines(scriptFilePath);
        ScriptRunner.runScript(connection, line, false, true);
        connection.close();
      } catch (IOException | SQLException e) {
        theLogger.error("Error while executing script file " + scriptFilePath.toString(), e);
      }
    }
  }

}
