package model.exercisereading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import model.JsonWrapper;
import model.exercise.Exercise;
import play.Logger;
import play.libs.Json;

public abstract class ExerciseReader<T extends Exercise> {
  
  private static final String EX_FILE_NAME = "exercises.json";
  private static final String EX_SCHEMA_FILE_NAME = "exerciseSchema.json";
  
  protected static final Logger.ALogger READING_LOGGER = Logger.of("startup");
  
  protected static final Path BASE_DIR = Paths.get("conf", "resources");
  
  protected String exerciseType;
  
  protected Path baseDirForExType;
  public Path baseTargetDir;
  
  protected Path jsonFile;
  protected Path jsonSchemaFile;
  
  public ExerciseReader(String theExerciseType) {
    exerciseType = theExerciseType;
    
    baseDirForExType = Paths.get(BASE_DIR.toString(), exerciseType);
    baseTargetDir = Paths.get("/data", "samples", exerciseType);
    
    jsonFile = Paths.get(baseDirForExType.toString(), EX_FILE_NAME);
    jsonSchemaFile = Paths.get(baseDirForExType.toString(), EX_SCHEMA_FILE_NAME);
  }
  
  public static String readFile(Path file) {
    try {
      return String.join("\n", Files.readAllLines(file));
    } catch (IOException e) {
      Logger.error("Error while reading file " + file, e);
      return "";
    }
  }

  protected static boolean createDirectory(Path directory) {
    try {
      Files.createDirectories(directory);
      return true;
    } catch (IOException e) {
      Logger.error("Error while creating sample file directory \"" + directory.toString() + "\"", e);
      return false;
    }
  }
  
  protected static String readTextArray(JsonNode textArray) {
    return String.join("", JsonWrapper.parseJsonArrayNode(textArray));
  }
  
  public AbstractReadingResult<T> readExercises(Path jsonFile) {
    JsonNode json = Json.parse(readFile(jsonFile));
    JsonNode jsonSchema = Json.parse(readFile(jsonSchemaFile));
    
    // Validate json with json schema
    Optional<ProcessingReport> report = JsonWrapper.validateJson(json, jsonSchema);
    if(!report.isPresent())
      return new ReadingError<>(Json.prettyPrint(json), Collections.emptyList());
    
    if(!report.get().isSuccess())
      return new ReadingError<>(Json.prettyPrint(json),
          StreamSupport.stream(report.get().spliterator(), false).collect(Collectors.toList()));
    
    List<T> exercises = StreamSupport.stream(json.spliterator(), false).map(this::readExercise)
        .collect(Collectors.toList());

    return new ReadingResult<>(Json.prettyPrint(json), exercises);
  }
  
  public AbstractReadingResult<T> readStandardExercises() {
    return readExercises(jsonFile);
  }
  
  public abstract void saveExercise(T exercise);
  
  protected abstract T readExercise(JsonNode exerciseNode);
  
}
