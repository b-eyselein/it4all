package model.exercisereading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import model.JsonWrapper;
import model.WithId;
import play.Logger;
import play.libs.Json;

public abstract class ExerciseReader<T extends WithId> {

  private static final String EX_FILE_NAME = "exercises.json";
  private static final String EX_SCHEMA_FILE_NAME = "exerciseSchema.json";

  protected static final Logger.ALogger READING_LOGGER = Logger.of("startup");

  protected String exerciseType;

  protected Path baseDirForExType;
  public Path baseTargetDir;

  protected Path jsonFile;
  protected Path jsonSchemaFile;

  public ExerciseReader(String theExerciseType) {
    exerciseType = theExerciseType;

    baseDirForExType = Paths.get("conf", "resources", exerciseType);
    baseTargetDir = Paths.get("/data", "samples", exerciseType);

    jsonSchemaFile = Paths.get(baseDirForExType.toString(), EX_SCHEMA_FILE_NAME);
  }

  public static boolean createDirectory(Path directory) {
    try {
      Files.createDirectories(directory);
      return true;
    } catch (IOException e) {
      Logger.error("Error while creating sample file directory \"" + directory.toString() + "\"", e);
      return false;
    }
  }

  public static String readFile(Path file) {
    try {
      return String.join("\n", Files.readAllLines(file));
    } catch (IOException e) {
      Logger.error("Error while reading file " + file, e);
      return "";
    }
  }

  public String getExerciseType() {
    return exerciseType;
  }

  public Path getJsonSchemaFile() {
    return jsonSchemaFile;
  }

  public AbstractReadingResult readAllFromFile(Path jsonFile) {
    String jsonAsString = readFile(jsonFile);
    String jsonSchemaAsString = readFile(jsonSchemaFile);

    JsonNode json = Json.parse(jsonAsString);

    // Validate json with json schema
    ProcessingReport report = JsonWrapper.validateJson(json, Json.parse(jsonSchemaAsString));
    if(report == null || !report.isSuccess())
      return new ReadingError(jsonAsString, jsonSchemaAsString, report);

    List<T> exercises = StreamSupport.stream(json.spliterator(), true).map(this::read).collect(Collectors.toList());

    return new ReadingResult<>(jsonAsString, jsonSchemaAsString, exercises);
  }

  public AbstractReadingResult readFromStandardFile() {
    return readAllFromFile(Paths.get(baseDirForExType.toString(), EX_FILE_NAME));
  }

  public abstract void saveRead(T exercise);

  protected abstract T read(JsonNode exerciseNode);

}
