package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.JsExercise.JsDataType;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.libs.Json;

public class JsExerciseReader extends ExerciseReader<JsExercise> {

  private static void handleTest(JsExercise exercise, JsonNode testNode) {
    int testId = testNode.get("id").asInt();
    JsTestKey key = new JsTestKey(exercise.id, testId);
    JsTest test = JsTest.finder.byId(key);

    if(test == null)
      test = new JsTest(key);

    test.inputs = testNode.get("input").asText();
    test.output = testNode.get("output").asText();
    test.save();
  }

  private static JsExercise readJsExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get("id").asInt();
    JsExercise exercise = JsExercise.finder.byId(id);

    if(exercise == null)
      exercise = new JsExercise(id);

    exercise.title = exerciseNode.get("title").asText();
    exercise.text = exerciseNode.get("text").asText();
    exercise.declaration = exerciseNode.get("declaration").asText();
    exercise.functionname = exerciseNode.get("functionname").asText();
    exercise.sampleSolution = exerciseNode.get("samplesolution").asText();
    exercise.inputtypes = exerciseNode.get("inputtypes").asText();
    exercise.inputcount = exerciseNode.get("inputcount").asInt();
    exercise.returntype = JsDataType.valueOf(exerciseNode.get("returntype").asText());
    
    for(final Iterator<JsonNode> testIter = exerciseNode.get("tests").elements(); testIter.hasNext();)
      handleTest(exercise, testIter.next());

    return exercise;
  }

  @Override
  public List<JsExercise> readExercises(Path jsonFile, Path jsonSchemaFile) {
    // FIXME: Auto-generated method stub
    List<JsExercise> exercises = new LinkedList<>();

    JsonNode json;
    JsonNode jsonSchema;
    try {
      String jsonAsString = String.join("\n", Files.readAllLines(jsonFile));
      String jsonSchemaAsString = String.join("\n", Files.readAllLines(jsonSchemaFile));

      json = Json.parse(jsonAsString);
      jsonSchema = Json.parse(jsonSchemaAsString);
    } catch (RuntimeException | IOException e) {
      Logger.error("Fehler beim Lesen aus der Datei " + exercises.toString(), e);
      return exercises;
    }

    // Validate json with schema
    if(!validateJson(json, jsonSchema))
      return exercises;

    for(final Iterator<JsonNode> childNodes = json.elements(); childNodes.hasNext();)
      exercises.add(readJsExercise(childNodes.next()));

    return exercises;
  }
}
