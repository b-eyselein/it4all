package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class UmlExerciseReader extends ExerciseReader<UmlExercise> {

  public UmlExerciseReader() {
    super("uml");
  }

  public List<Mapping> readMappings(JsonNode mappingsNode) {
    return StreamSupport.stream(mappingsNode.spliterator(), true).map(Mapping::fromJson).collect(Collectors.toList());
  }

  @Override
  public void saveExercise(UmlExercise exercise) {
    exercise.save();
  }

  @Override
  protected UmlExercise readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    UmlExercise exercise = UmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new UmlExercise(id);

    String rawText = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");

    exercise.mappings = readMappings(exerciseNode.get("mappings"));

    exercise.title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    exercise.text = rawText;

    UmlExTextParser parser = new UmlExTextParser(rawText, exercise.mappings,
        JsonWrapper.parseJsonArrayNode(exerciseNode.get("ignore")));
    exercise.classSelText = parser.parseTextForClassSel();
    exercise.diagDrawText = parser.parseTextForDiagDrawing();

    // Save solution as json in db
    exercise.solution = exerciseNode.get(StringConsts.SOLUTION_NAME).toString();

    return exercise;
  }

}
