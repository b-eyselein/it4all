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
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import io.ebean.Finder;
import model.JsonReadable;
import model.StringConsts;
import model.exercise.Exercise;
import model.exercise.ExerciseState;
import play.Logger;
import play.data.DynamicForm;

public abstract class ExerciseReader<E extends Exercise> extends JsonReader<E> {
  
  private static final JsonSchemaGenerator JSON_SCHEMA_GENERATOR = new JsonSchemaGenerator(new ObjectMapper());
  
  public Path baseTargetDir;
  
  public ExerciseReader(String theExerciseType, Finder<Integer, E> theFinder, Class<?> theClassFor) {
    super(theExerciseType, theFinder, theClassFor);
    baseTargetDir = Paths.get("/data", "samples", exerciseType());
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
  
  public static <T extends JsonReadable> int findMinimalNotUsedId(Finder<Integer, T> finder) {
    // FIXME: this is probably a ugly hack...
    final List<T> exercises = finder.all();
    
    Collections.sort(exercises, (q1, q2) -> q1.getId() - q2.getId());
    
    if(exercises.isEmpty())
      return 1;
    
    for(int i = 0; i < exercises.size() - 1; i++)
      if(exercises.get(i).getId() < exercises.get(i + 1).getId() - 1)
        return exercises.get(i).getId() + 1;
      
    return exercises.get(exercises.size() - 1).getId() + 1;
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
  
  public String getExerciseType() {
    return exerciseType();
  }
  
  public JsonNode getJsonSchema() {
    return jsonSchema();
  }
  
  public E getOrInstantiateExercise(int id) {
    return Optional.ofNullable(finder().byId(id)).orElse(instantiateExercise(id));
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
    return super.readFromJsonFile(jsonFile);
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
  
  public abstract void saveExercise(E exercise);
  
  protected abstract void updateExercise(E exercise, JsonNode exerciseNode);
  
}
