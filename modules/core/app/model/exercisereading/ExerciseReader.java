package model.exercisereading;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import model.exercise.Exercise;
import play.Logger;
import play.libs.Json;

public abstract class ExerciseReader<T extends Exercise> {

  private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.byDefault();

  private static boolean validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode) {
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

  public List<T> readExercises(Path jsonFile, Path jsonSchemaFile) {
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

  protected abstract T readExercise(JsonNode exerciseNode);

}
