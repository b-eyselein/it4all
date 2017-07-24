package model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import play.Logger;

public class UmlExerciseReader extends ExerciseReader<UmlExercise> {

  private static final UmlExerciseReader INSTANCE = new UmlExerciseReader();

  private UmlExerciseReader() {
    super("uml");
  }

  public static UmlExerciseReader getInstance() {
    return INSTANCE;
  }

  public List<Mapping> readMappings(JsonNode mappingsNode) {
    return readArray(mappingsNode, Mapping::fromJson);
  }

  @Override
  public void saveRead(UmlExercise exercise) {
    Logger.debug(exercise.toString());
    exercise.save();
  }

  @Override
  protected UmlExercise read(JsonNode exerciseNode) {
    // FIXME: implement!
    // exercise.mappings = readMappings(exerciseNode.get("mappings"));

    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();
    String title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    String author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    String text = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");

    UmlExTextParser parser = new UmlExTextParser(text,
        Collections.emptyList() /* exercise.mappings */, JsonWrapper.parseJsonArrayNode(exerciseNode.get("ignore")));
    String classSelText = parser.parseTextForClassSel();
    String diagDrawText = parser.parseTextForDiagDrawing();

    // Save solution as json in db
    String solution = exerciseNode.get(StringConsts.SOLUTION_NAME).asText();

    UmlExercise exercise = UmlExercise.finder.byId(id);
    if(exercise == null)
      return new UmlExercise(id, title, author, text, classSelText, diagDrawText, solution);
    else
      return exercise.updateValues(id, title, author, text, classSelText, diagDrawText, solution);
  }

}
