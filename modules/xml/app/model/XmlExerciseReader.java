package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.XmlExercise.XmlExType;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.libs.Json;

public class XmlExerciseReader extends ExerciseReader<XmlExercise> {

  @Override
  public List<XmlExercise> readExercises(Path jsonFile, Path jsonSchemaFile) {
    // TODO Auto-generated method stub
    List<XmlExercise> exercises = new LinkedList<>();

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
      exercises.add(readExercise(childNodes.next()));

    return exercises;
  }

  private XmlExercise readExercise(JsonNode node) {
    JsonNode idNode = node.get("id");
    int id = idNode.asInt();

    XmlExercise exercise = XmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new XmlExercise();

    exercise.id = id;
    exercise.title = node.get("title").asText();
    exercise.exerciseType = XmlExType.valueOf(node.get("exerciseType").asText());
    exercise.referenceFileName = node.get("referenceFileName").asText();
    exercise.exerciseText = node.get("exerciseText").asText();
    exercise.fixedStart = node.get("fixedStart").asText();
    return exercise;
  }

}
