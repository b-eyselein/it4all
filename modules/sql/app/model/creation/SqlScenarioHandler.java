package model.creation;

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
import model.ScriptRunner;
import model.exercise.CreationResultType;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import play.Logger;
import play.db.Database;

public class SqlScenarioHandler {

  private static Logger.ALogger theLogger = Logger.of("startup");
  private static final String SCENARIO_FOLDER = "conf/resources/sql";

  // FIXME: genauere Fehlermeldungen (auch auf Konsole --> Logger!)
  // BEISPIEL: Aufgabe nicht erstellt, weil "text" oder "sampleSolutions"
  // fehlt/falsch

  public static ScenarioCreationResult handleScenario(Path path, Database database) {
    JsonNode json = null;
    try {
      json = StartUpChecker.readJsonFile(path);
    } catch (IOException e) {
      Logger.error("Error while reading Sql scenario file \"" + path + "\":", e);
      return new ScenarioCreationResult(CreationResultType.FAILURE, e.getMessage(), null);
    }

    ScenarioCreationResult scenarioResult = readAndUpdateScenario(json);

    if(scenarioResult.getResultType() == CreationResultType.FAILURE)
      return scenarioResult;

    SqlScenario scenario = scenarioResult.getCreated();

    JsonNode exerciseNodes = json.get("exercises");

    if(exerciseNodes == null) {
      theLogger.debug("There were no exercises specified in file " + path);
      return scenarioResult;
    }

    List<ExerciseCreationResult> readExercises = handleExercises(scenario, exerciseNodes);
    scenarioResult.addExerciseResults(readExercises);

    // FIXME: run script to create database
    // runCreateScript(database, scenario);

    return scenarioResult;
  }

  private static List<ExerciseCreationResult> handleExercises(SqlScenario scenario, JsonNode exerciseNodes) {
    List<ExerciseCreationResult> exercises = new LinkedList<>();

    for(final Iterator<String> exerciseTypesIter = exerciseNodes.fieldNames(); exerciseTypesIter.hasNext();) {
      String exerciseTypeAsString = exerciseTypesIter.next();

      SqlExerciseType exerciseType = SqlExerciseType.valueOf(exerciseTypeAsString);
      if(exerciseType != null)
        exercises.addAll(handleExercisesForType(scenario, exerciseType, exerciseNodes.get(exerciseTypeAsString)));
    }

    return exercises;
  }

  private static List<ExerciseCreationResult> handleExercisesForType(SqlScenario scenario, SqlExerciseType exerciseType,
      JsonNode exercisesNodes) {
    List<ExerciseCreationResult> exercises = new LinkedList<>();

    for(final Iterator<String> exerciseFieldIter = exercisesNodes.fieldNames(); exerciseFieldIter.hasNext();) {
      String exerciseIdAsString = exerciseFieldIter.next();
      int exerciseId = Integer.parseInt(exerciseIdAsString);
      JsonNode exerciseNode = exercisesNodes.get(exerciseIdAsString);
      exercises.add(readExercise(scenario, exerciseId, exerciseType, exerciseNode));
    }
    return exercises;
  }

  private static ScenarioCreationResult readAndUpdateScenario(JsonNode json) {
    JsonNode shortNameNode = json.get("shortName");
    JsonNode longNameNode = json.get("longName");
    JsonNode scriptFileNode = json.get("scriptFile");

    if(shortNameNode == null || longNameNode == null || scriptFileNode == null)
      return new ScenarioCreationResult(CreationResultType.FAILURE,
          "Einer oder mehrere der drei Knoten \"shortName\", \"longName\" und \"scriptFile\" "
              + "fehlen in der Definition des Szenarios!",
          null);

    String shortName = shortNameNode.asText();
    String newLongName = longNameNode.asText();
    String newScriptFile = scriptFileNode.asText();

    SqlScenario scenario = SqlScenario.finder.byId(shortName);
    if(scenario == null) {
      scenario = new SqlScenario(shortName);
      scenario.longName = newLongName;
      scenario.scriptFile = newScriptFile;
      return new ScenarioCreationResult(CreationResultType.NEW, "", scenario);
    }

    CreationResultType resultType = CreationResultType.NOT_UPDATED;
    if(!scenario.longName.equals(newLongName) || !scenario.scriptFile.equals(newScriptFile))
      resultType = CreationResultType.TO_UPDATE;

    scenario.longName = newLongName;
    scenario.scriptFile = newScriptFile;

    return new ScenarioCreationResult(resultType, "", scenario);
  }

  private static ExerciseCreationResult readExercise(SqlScenario scenario, int exerciseId, SqlExerciseType exerciseType,
      JsonNode exerciseNode) {
    // FIXME: parse sample solutions to find errors?!?
    SqlExerciseKey exerciseKey = new SqlExerciseKey(scenario.shortName, exerciseId, exerciseType);
    SqlExercise exercise = SqlExercise.finder.byId(exerciseKey);

    // Update text and samples olutions, key and type remain the same
    JsonNode textNode = exerciseNode.get("text");
    JsonNode sampleSolutionsNode = exerciseNode.get("sampleSolutions");

    if(textNode == null || sampleSolutionsNode == null)
      return new ExerciseCreationResult(CreationResultType.FAILURE,
          "Einer der beiden Knoten \"text\" oder \"sampleSolutions\" fehlt!", exerciseId, null);

    String newText = textNode.asText(), newSamples = readSampleSolutions(sampleSolutionsNode);

    if(exercise == null) {
      exercise = new SqlExercise(exerciseKey);
      exercise.text = newText;
      exercise.samples = newSamples;
      readExtra(exerciseType, exercise, exerciseNode);
      return new ExerciseCreationResult(CreationResultType.NEW, "", exerciseId, exercise);
    }

    CreationResultType resultType = CreationResultType.NOT_UPDATED;
    if(!exercise.text.equals(newText) || !exercise.samples.equals(newSamples))
      resultType = CreationResultType.TO_UPDATE;
    exercise.text = newText;
    exercise.samples = newSamples;

    boolean updated = readExtra(exerciseType, exercise, exerciseNode);

    if(updated)
      resultType = CreationResultType.TO_UPDATE;

    return new ExerciseCreationResult(resultType, "", exerciseId, exercise);
  }

  private static boolean readExtra(SqlExerciseType exerciseType, SqlExercise exercise, JsonNode exerciseNode) {
    JsonNode validationNode = exerciseNode.get("validation");
    if(validationNode == null)
      return false;

    String newValidation = validationNode.asText();
    if(exercise.validation != null && exercise.validation.equals(newValidation))
      return false;

    exercise.validation = validationNode.asText();
    return true;
  }

  private static String readSampleSolutions(JsonNode sampleSolutions) {
    List<String> samples = new LinkedList<>();
    sampleSolutions.elements().forEachRemaining(el -> samples.add(el.asText()));
    return String.join(SqlExercise.SAMPLE_JOIN_CHAR, samples);
  }

  @SuppressWarnings("unused")
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
