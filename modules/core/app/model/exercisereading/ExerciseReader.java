package model.exercisereading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import model.Util;
import model.exercise.Exercise;
import play.Logger;
import play.libs.Json;

public abstract class ExerciseReader<T extends Exercise> {

  private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.byDefault();
  private static final String EX_FILE_NAME = "exercises.json";
  private static final String EX_SCHEMA_FILE_NAME = "exerciseSchema.json";

  protected static final Logger.ALogger READING_LOGGER = Logger.of("startup");

  protected static final String BASE_DIR = "conf/resources";

  protected String exerciseType;

  protected Path jsonFile;
  protected Path jsonSchemaFile;

  public ExerciseReader(String theExerciseType) {
    exerciseType = theExerciseType;
    jsonFile = Paths.get(BASE_DIR, exerciseType, EX_FILE_NAME);
    jsonSchemaFile = Paths.get(BASE_DIR, exerciseType, EX_SCHEMA_FILE_NAME);
  }

  protected static boolean validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode) {
    try {
      ProcessingReport report = FACTORY.getJsonSchema(exercisesSchemaNode).validate(exercisesNode);

      if(!report.isSuccess()) {
        // report errors
        List<String> messages = new LinkedList<>();
        report.forEach(mes -> messages.add(mes.toString()));
        Logger.error("There have been errors validating a JSON file:\n" + String.join("\n", messages));
      }

      return report.isSuccess();
    } catch (ProcessingException e) {
      Logger.error("There has been an error validating a JSON file!", e);
      return false;
    }
  }

  public List<T> readExercises(Path jsonFile) {
    try {
      JsonNode json = Json.parse(String.join("\n", Files.readAllLines(jsonFile)));
      JsonNode jsonSchema = Json.parse(String.join("\n", Files.readAllLines(jsonSchemaFile)));

      // Validate json with schema
      if(!validateJson(json, jsonSchema))
        return Collections.emptyList();

      List<T> exercises = new LinkedList<>();

      for(final Iterator<JsonNode> childNodes = json.elements(); childNodes.hasNext();)
        exercises.add(readExercise(childNodes.next()));

      return exercises;
    } catch (Exception e) {
      Logger.error("Fehler beim Lesen aus der Datei " + jsonFile.toString() + " or " + jsonSchemaFile.toString(), e);
      return Collections.emptyList();
    }
  }

  public List<T> readStandardExercises() {
    return readExercises(jsonFile);
  }

  protected void createSampleDirectory(Util util) {
    try {
      Path sampleDirectory = util.getSampleDirForExerciseType(exerciseType);
      if(!sampleDirectory.toFile().exists())
        Files.createDirectories(sampleDirectory);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      Logger.error("Error while creating sample file directory for " + exerciseType, e);
    }
  }

  protected abstract T readExercise(JsonNode exerciseNode);

}
