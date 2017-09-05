package model.exercisereading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import model.WithId;
import model.exercise.Exercise;
import model.exercise.ExerciseState;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;

public abstract class ExerciseReader<E extends Exercise> {
  
  private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.byDefault();
  
  private static final JsonSchemaGenerator JSON_SCHEMA_GENERATOR = new JsonSchemaGenerator(new ObjectMapper());
  
  protected final Finder<Integer, E> finder;
  
  protected String exerciseType;
  
  protected JsonNode jsonSchema;
  
  public Path baseTargetDir;
  
  public ExerciseReader(String theExerciseType, Finder<Integer, E> theFinder, Class<?> theClassFor) {
    exerciseType = theExerciseType;
    finder = theFinder;
    
    baseTargetDir = Paths.get("/data", "samples", exerciseType);
    
    jsonSchema = parseJsonSchema(theClassFor);
  }
  
  public static boolean createDirectory(Path directory) {
    try {
      Files.createDirectories(directory);
      return true;
    } catch (final IOException e) {
      Logger.error("Error while creating sample file directory \"" + directory.toString() + "\"", e);
      return false;
    }
  }
  
  public static <T extends WithId> int findMinimalNotUsedId(Finder<Integer, T> finder) {
    // FIXME: this is probably a ugly hack...
    final List<T> questions = finder.all();
    
    Collections.sort(questions);
    
    if(questions.isEmpty())
      return 1;
    
    for(int i = 0; i < questions.size() - 1; i++)
      if(questions.get(i).getId() < questions.get(i + 1).getId() - 1)
        return questions.get(i).getId() + 1;
      
    return questions.get(questions.size() - 1).getId() + 1;
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
    } catch (final IOException e) {
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
  
  public E getOrInstantiateExercise(int id) {
    return Optional.ofNullable(finder.byId(id)).orElse(instantiateExercise(id));
  }
  
  public E initFromForm(int id, DynamicForm form) {
    final E exercise = getOrInstantiateExercise(id);
    
    exercise.setTitle(form.get(StringConsts.TITLE_NAME));
    exercise.setAuthor(form.get(StringConsts.AUTHOR_NAME));
    exercise.setText(form.get(StringConsts.TEXT_NAME));
    
    initRemainingExFromForm(exercise, form);
    
    return exercise;
  }
  
  public abstract void initRemainingExFromForm(E exercise, DynamicForm form);
  
  public JsonNode parseJsonSchema(Class<?> classFor) {
    return JSON_SCHEMA_GENERATOR.generateJsonSchema(classFor);
  }
  
  public AbstractReadingResult readAllFromFile(Path jsonFile) {
    final String jsonAsString = readFile(jsonFile);
    final JsonNode json = Json.parse(jsonAsString);
    
    // Validate json with json schema
    try {
      final ProcessingReport report = validateJson(json, jsonSchema);
      if(!report.isSuccess())
        return new ReadingError(jsonAsString, Json.prettyPrint(jsonSchema), report);
    } catch (final ProcessingException e) {
      return new ReadingFailure(jsonAsString, Json.prettyPrint(jsonSchema), e);
    }
    
    final List<E> exercises = StreamSupport.stream(json.spliterator(), true).map(this::readExercise)
        .collect(Collectors.toList());
    
    return new ReadingResult<>(jsonAsString, Json.prettyPrint(jsonSchema), exercises);
  }
  
  public E readExercise(JsonNode exerciseNode) {
    final E exercise = getOrInstantiateExercise(exerciseNode.get(StringConsts.ID_NAME).asInt());
    
    exercise.setTitle(exerciseNode.get(StringConsts.TITLE_NAME).asText());
    exercise.setAuthor(exerciseNode.get(StringConsts.AUTHOR_NAME).asText());
    exercise.setText(readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), ""));
    exercise.setState(ExerciseState.CREATED);
    
    updateExercise(exercise, exerciseNode);
    
    return exercise;
  }
  
  public AbstractReadingResult readFromStandardFile() {
    return readAllFromFile(Paths.get("conf", "resources", exerciseType, StringConsts.EX_FILE_NAME));
  }
  
  public abstract void saveExercise(E exercise);
  
  protected abstract E instantiateExercise(int id);
  
  protected abstract void updateExercise(E exercise, JsonNode exerciseNode);
  
}
