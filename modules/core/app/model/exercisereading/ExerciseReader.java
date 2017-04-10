package model.exercisereading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import model.JsonWrapper;
import model.Util;
import model.exercise.Exercise;
import play.Logger;
import play.libs.Json;

public abstract class ExerciseReader<T extends Exercise> {

  private static final String EX_FILE_NAME = "exercises.json";
  private static final String EX_SCHEMA_FILE_NAME = "exerciseSchema.json";

  protected static final Logger.ALogger READING_LOGGER = Logger.of("startup");

  protected static final String BASE_DIR = "conf/resources";

  protected static final String ID_NAME = "id";
  protected static final String KEY_NAME = "key";
  protected static final String TEXT_NAME = "text";
  protected static final String TITLE_NAME = "title";

  protected String exerciseType;

  protected Path jsonFile;
  protected Path jsonSchemaFile;

  public ExerciseReader(String theExerciseType) {
    exerciseType = theExerciseType;
    jsonFile = Paths.get(BASE_DIR, exerciseType, EX_FILE_NAME);
    jsonSchemaFile = Paths.get(BASE_DIR, exerciseType, EX_SCHEMA_FILE_NAME);
  }

  protected void createSampleDirectory(Util util) {
    try {
      Path sampleDirectory = util.getSampleDirForExerciseType(exerciseType);
      if(!sampleDirectory.toFile().exists())
        Files.createDirectories(sampleDirectory);
    } catch (IOException e) {
      Logger.error("Error while creating sample file directory for " + exerciseType, e);
    }
  }

  protected abstract T readExercise(JsonNode exerciseNode);

  public AbstractReadingResult readExercises(Path jsonFile) {
    try {
      JsonNode json = Json.parse(String.join("\n", Files.readAllLines(jsonFile)));
      JsonNode jsonSchema = Json.parse(String.join("\n", Files.readAllLines(jsonSchemaFile)));

      // Validate json with json schema
      ProcessingReport report = JsonWrapper.validateJson(json, jsonSchema);
      if(!report.isSuccess())
        return new ReadingError(Json.prettyPrint(json),
            StreamSupport.stream(report.spliterator(), false).collect(Collectors.toList()));

      List<T> exercises = new LinkedList<>();

      for(final Iterator<JsonNode> childNodes = json.elements(); childNodes.hasNext();)
        exercises.add(readExercise(childNodes.next()));

      return new ReadingResult<T>(Json.prettyPrint(json), exercises);
    } catch (ProcessingException | IOException e) {
      String error = "Fehler beim Lesen aus der Datei " + jsonFile.toString() + " or " + jsonSchemaFile.toString();
      Logger.error(error, e);
      return new ReadingError("", Collections.emptyList());
    }
  }

  public AbstractReadingResult readStandardExercises() {
    return readExercises(jsonFile);
  }

}
