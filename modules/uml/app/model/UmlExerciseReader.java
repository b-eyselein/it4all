package model;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import play.data.DynamicForm;

public class UmlExerciseReader extends ExerciseReader<UmlExercise> {

  private static final UmlExerciseReader INSTANCE = new UmlExerciseReader();

  private UmlExerciseReader() {
    super("uml", UmlExercise.finder, UmlExercise[].class);
  }

  public static UmlExerciseReader getInstance() {
    return INSTANCE;
  }

  @Override
  public void initRemainingExFromForm(UmlExercise exercise, DynamicForm form) {
    exercise.setClassSelText(form.get("classSelText"));
    exercise.setDiagDrawText(form.get("diagDrawText"));
    exercise.setSolution(form.get("solution"));
  }

  @Override
  public UmlExercise instantiateExercise(int id) {
    return new UmlExercise(id);
  }

  @Override
  protected void updateExercise(UmlExercise exercise, JsonNode exerciseNode) {
    final String text = "";

    exercise.setMappings(readArray(exerciseNode.get(StringConsts.MAPPINGS_NAME), Mapping::fromJson));
    exercise.setToIgnore(readTextArray(exerciseNode.get(StringConsts.IGNORE_NAME), "#"));

    final UmlExTextParser parser = new UmlExTextParser(text, exercise.getMappings(), exercise.getToIgnore());
    exercise.setClassSelText(parser.parseTextForClassSel());
    exercise.setDiagDrawText(parser.parseTextForDiagDrawing());

    // Save solution as json in db
    exercise.setSolution(exerciseNode.get(StringConsts.SOLUTION_NAME).asText());
  }

}
