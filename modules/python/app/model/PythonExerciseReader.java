package model;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class PythonExerciseReader extends ExerciseReader<PythonExercise> {

  public PythonExerciseReader() {
    super("python");
  }
  
  @Override
  protected PythonExercise readExercise(JsonNode exerciseNode) {
    // TODO Auto-generated method stub
    return null;
  }

}
