package model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class UmlExerciseReader extends ExerciseReader<UmlExercise> {

  private static final UmlExerciseReader INSTANCE = new UmlExerciseReader();

  private UmlExerciseReader() {
    super("uml", UmlExercise.finder, UmlExercise[].class);
  }

  public static UmlExerciseReader getInstance() {
    return INSTANCE;
  }

  @Override
  public void saveRead(UmlExercise exercise) {
    exercise.save();
  }

  @Override
  protected UmlExercise instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    List<Mapping> mappings = readArray(exerciseNode.get(StringConsts.MAPPINGS_NAME), Mapping::fromJson);
    List<String> ignore = parseJsonArrayNode(exerciseNode.get(StringConsts.IGNORE_NAME));

    UmlExTextParser parser = new UmlExTextParser(text, mappings, ignore);
    String classSelText = parser.parseTextForClassSel();
    String diagDrawText = parser.parseTextForDiagDrawing();

    // Save solution as json in db
    String solution = exerciseNode.get(StringConsts.SOLUTION_NAME).asText();

    return new UmlExercise(id, title, author, text, classSelText, diagDrawText, solution, mappings, ignore);
  }

}
