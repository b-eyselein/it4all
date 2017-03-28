package model;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class UmlExerciseReader extends ExerciseReader<UmlExercise> {

  public UmlExerciseReader() {
    super("uml");
  }

  private static String readFile(String file) {
    return "TODO: read file...";
  }

  @Override
  protected UmlExercise readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get("id");
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode rawtextNode = exerciseNode.get("rawtext");

    int id = idNode.asInt();
    UmlExercise exercise = UmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new UmlExercise(id);

    String rawText = readFile(rawtextNode.asText());

    exercise.classSelText = rawText;
    exercise.diagDrawHelpText = rawText;
    exercise.diagDrawText = rawText;
    exercise.text = rawText;
    exercise.title = titleNode.asText();

    return exercise;
  }

}
