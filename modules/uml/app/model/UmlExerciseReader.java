package model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class UmlExerciseReader extends ExerciseReader<UmlExercise> {

  public UmlExerciseReader() {
    super("uml");
  }

  @Override
  protected UmlExercise readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get(ID_NAME);
    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode rawtextNode = exerciseNode.get(TEXT_NAME);
    JsonNode mappingsNode = exerciseNode.get("mappings");
    JsonNode ingoreNode = exerciseNode.get("ignore");

    int id = idNode.asInt();
    UmlExercise exercise = UmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new UmlExercise(id);

    String rawText = rawtextNode.asText();

    Map<String, String> mappings = JsonWrapper.readKeyValueMap(mappingsNode);
    List<String> toIgnore = JsonWrapper.parseJsonArrayNode(ingoreNode);

    UmlExTextParser parser = new UmlExTextParser(rawText, mappings, toIgnore);

    exercise.classSelText = parser.parseTextForClassSel();
    exercise.diagDrawHelpText = rawText;
    exercise.diagDrawText = rawText;
    exercise.text = rawText;
    exercise.title = titleNode.asText();

    return exercise;
  }

}
