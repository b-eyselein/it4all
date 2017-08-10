package model;

import java.util.Collections;
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
  
  public List<Mapping> readMappings(JsonNode mappingsNode) {
    return readArray(mappingsNode, Mapping::fromJson);
  }
  
  @Override
  public void saveRead(UmlExercise exercise) {
    exercise.save();
  }
  
  @Override
  protected UmlExercise instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    UmlExTextParser parser = new UmlExTextParser(text,
        Collections.emptyList() /* exercise.mappings */, parseJsonArrayNode(exerciseNode.get("ignore")));
    String classSelText = parser.parseTextForClassSel();
    String diagDrawText = parser.parseTextForDiagDrawing();
    
    // Save solution as json in db
    String solution = exerciseNode.get(StringConsts.SOLUTION_NAME).asText();
    
    return new UmlExercise(id, title, author, text, classSelText, diagDrawText, solution);
  }
  
}
