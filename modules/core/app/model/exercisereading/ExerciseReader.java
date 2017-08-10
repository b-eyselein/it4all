package model.exercisereading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import io.ebean.Finder;
import model.StringConsts;
import model.exercise.Exercise;
import play.Logger;
import play.libs.Json;

public abstract class ExerciseReader<T extends Exercise> {

  private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.byDefault();

  private static final JsonSchemaGenerator JSON_SCHEMA_GENERATOR = new JsonSchemaGenerator(new ObjectMapper());

  protected final Finder<Integer, T> finder;

  protected String exerciseType;

  protected JsonNode jsonSchema;

  public Path baseTargetDir;

  public ExerciseReader(String theExerciseType, Finder<Integer, T> theFinder, Class<?> theClassFor) {
    exerciseType = theExerciseType;
    finder = theFinder;

    baseTargetDir = Paths.get("/data", "samples", exerciseType);

    jsonSchema = parseJsonSchema(theClassFor);
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

  public static List<String> parseJsonArrayNode(JsonNode node) {
    return StreamSupport.stream(node.spliterator(), true).map(JsonNode::asText).collect(Collectors.toList());
  }

  public static <V> List<V> readArray(JsonNode arrayNode, Function<JsonNode, V> mappingFunction) {
    return StreamSupport.stream(arrayNode.spliterator(), true).map(mappingFunction).collect(Collectors.toList());
  }

  public static String readFile(Path file) {
    try {
      return String.join("\n", Files.readAllLines(file));
    } catch (IOException e) {
      Logger.error("Error while reading file " + file, e);
      return "";
    }
  }

  public static String readTextArray(JsonNode textArray, String joinChar) {
    return String.join(joinChar, parseJsonArrayNode(textArray));
  }

  public static ProcessingReport validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode)
      throws ProcessingException {
    return FACTORY.getJsonSchema(exercisesSchemaNode).validate(exercisesNode);
  }

  public String getExerciseType() {
    return exerciseType;
  }

  public JsonNode getJsonSchema() {
    return jsonSchema;
  }

  public JsonNode parseJsonSchema(Class<?> classFor) {
    return JSON_SCHEMA_GENERATOR.generateJsonSchema(classFor);
  }

  public AbstractReadingResult readAllFromFile(Path jsonFile) {
    String jsonAsString = readFile(jsonFile);
    JsonNode json = Json.parse(jsonAsString);

    // Validate json with json schema
    try {
      ProcessingReport report = validateJson(json, jsonSchema);
      if(!report.isSuccess())
        return new ReadingError(jsonAsString, Json.prettyPrint(jsonSchema), report);
    } catch (ProcessingException e) {
      return new ReadingError(jsonAsString, Json.prettyPrint(jsonSchema), e.getMessage());
    }

    List<T> exercises = StreamSupport.stream(json.spliterator(), true).map(this::readExercise)
        .collect(Collectors.toList());

    return new ReadingResult<>(jsonAsString, Json.prettyPrint(jsonSchema), exercises);
  }

  public T readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    String title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    String author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    String text = readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");

    T exercise = finder.byId(id);

    if(exercise == null)
      return instantiateExercise(id, title, author, text, exerciseNode);

    exercise.updateValues(id, title, author, text, exerciseNode);
    return exercise;
  }

  public AbstractReadingResult readFromStandardFile() {
    return readAllFromFile(Paths.get("conf", "resources", exerciseType, StringConsts.EX_FILE_NAME));
  }

  public abstract void saveRead(T exercise);

  protected abstract T instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode);

}
