package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.db.Database;
import play.libs.Json;

public class SqlExerciseReader extends ExerciseReader<SqlExercise> {

  private static final String CREATE_DUMMY = "CREATE DATABASE IF NOT EXISTS ";

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

  private static void flushPrivileges(Connection connection) throws SQLException {
    Statement flushPrivileges = connection.createStatement();
    flushPrivileges.executeUpdate("FLUSH PRIVILEGES");
    flushPrivileges.close();
  }

  // FIXME: genauere Fehlermeldungen (auch auf Konsole --> Logger!)
  // BEISPIEL: Aufgabe nicht erstellt, weil "text" oder "sampleSolutions"
  // fehlt/falsch

  private static void grantRights(String databaseName, Connection connection) throws SQLException {
    try(Statement grantStatement = connection.createStatement()) {
      grantStatement.executeUpdate(
          "GRANT ALL PRIVILEGES ON " + databaseName + ".* TO " + "'it4all'@localhost IDENTIFIED BY 'c4aK3?bV';");
    } catch (SQLException e) {
      Logger.error("There has been an error running an sql script: \"" + CREATE_DUMMY + databaseName + "\"", e);
    }
  }

  private static SqlExercise readExercise(SqlScenario scenario, SqlExerciseType exerciseType, JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get("id");
    JsonNode textNode = exerciseNode.get("text");
    JsonNode sampleSolutionsNode = exerciseNode.get("samplesolutions");
    JsonNode validationNode = exerciseNode.get("validation");

    SqlExerciseKey exerciseKey = new SqlExerciseKey(scenario.shortName, idNode.asInt(), exerciseType);

    SqlExercise exercise = SqlExercise.finder.byId(exerciseKey);
    if(exercise == null)
      exercise = new SqlExercise(exerciseKey);

    exercise.text = textNode.asText();
    exercise.samples = readSampleSolutions(sampleSolutionsNode);
    if(validationNode != null)
      // TODO: eliminate!
      exercise.validation = validationNode.asText();

    return exercise;
  }

  private static List<SqlExercise> readExercises(SqlScenario scenario, JsonNode exerciseNodes) {
    List<SqlExercise> exercises = new LinkedList<>();

    for(final Iterator<String> exerciseTypesIter = exerciseNodes.fieldNames(); exerciseTypesIter.hasNext();) {
      String exerciseTypeAsString = exerciseTypesIter.next();

      SqlExerciseType exerciseType = SqlExerciseType.valueOf(exerciseTypeAsString);
      if(exerciseType != null)
        exercises.addAll(readExercisesForType(scenario, exerciseType, exerciseNodes.get(exerciseTypeAsString)));
    }

    return exercises;
  }

  private static List<SqlExercise> readExercisesForType(SqlScenario scenario, SqlExerciseType exerciseType,
      JsonNode exerciseTypeNodes) {
    List<SqlExercise> exercises = new LinkedList<>();

    for(final Iterator<JsonNode> exerciseNodesIter = exerciseTypeNodes.elements(); exerciseNodesIter.hasNext();)
      exercises.add(readExercise(scenario, exerciseType, exerciseNodesIter.next()));

    return exercises;
  }

  private static String readSampleSolutions(JsonNode sampleSolutions) {
    List<String> samples = new LinkedList<>();
    sampleSolutions.elements().forEachRemaining(el -> samples.add(el.asText()));
    return String.join(SqlExercise.SAMPLE_JOIN_CHAR, samples);
  }

  public List<SqlScenario> readScenarioes(Path jsonFile) {
    try {
      JsonNode json = Json.parse(String.join("\n", Files.readAllLines(jsonFile)));
      JsonNode jsonSchema = Json.parse(String.join("\n", Files.readAllLines(jsonSchemaFile)));

      // Validate json with schema
      if(!validateJson(json, jsonSchema))
        return Collections.emptyList();

      List<SqlScenario> results = new LinkedList<>();

      for(Iterator<JsonNode> scenarioNodeIter = json.elements(); scenarioNodeIter.hasNext();)
        results.add(readScenario(scenarioNodeIter.next()));

      return results;
    } catch (IOException e) {
      Logger.error("Fehler beim Lesen aus der Datei " + jsonFile.toString() + " or " + jsonSchemaFile.toString(), e);
      return Collections.emptyList();
    }
  }

  public List<SqlScenario> readStandardScenarioes() {
    return readScenarioes(jsonFile);

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

  private SqlScenario readScenario(JsonNode json) {
    JsonNode shortNameNode = json.get("shortname");
    JsonNode longNameNode = json.get("longname");
    JsonNode scriptFileNode = json.get("scriptfile");
    JsonNode exerciseNodes = json.get("exercises");

    String shortName = shortNameNode.asText();

    SqlScenario scenario = SqlScenario.finder.byId(shortName);
    if(scenario == null)
      scenario = new SqlScenario(shortName);

    scenario.longName = longNameNode.asText();
    scenario.scriptFile = scriptFileNode.asText();
    scenario.exercises = readExercises(scenario, exerciseNodes);

    return scenario;
  }

  @Override
  protected SqlExercise readExercise(JsonNode exerciseNode) {
    // TODO Auto-generated method stub
    return null;
  }

}
