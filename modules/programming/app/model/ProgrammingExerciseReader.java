package model;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class ProgrammingExerciseReader extends ExerciseReader<ProgrammingExercise> {

  public ProgrammingExerciseReader() {
    super("prog");
  }

  @Override
  protected ProgrammingExercise readExercise(JsonNode exerciseNode) {
    // TODO Auto-generated method stub
    return null;
  }

}
