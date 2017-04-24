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
    int id = exerciseNode.get(ID_NAME).asInt();
    UmlExercise exercise = UmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new UmlExercise(id);

    String rawText = exerciseNode.get(TEXT_NAME).asText();

    Map<String, String> mappings = JsonWrapper.readKeyValueMap(exerciseNode.get("mappings"));
    List<String> toIgnore = JsonWrapper.parseJsonArrayNode(exerciseNode.get("ignore"));

    exercise.title = exerciseNode.get(TITLE_NAME).asText();
    exercise.text = rawText;

    UmlExTextParser parser = new UmlExTextParser(rawText, mappings, toIgnore);
    exercise.classSelText = parser.parseTextForClassSel();
    exercise.diagDrawText = parser.parseTextForDiagDrawing();

    // Save solution as json in db
    exercise.solution = exerciseNode.get("solution").toString();

    return exercise;
  }

}
