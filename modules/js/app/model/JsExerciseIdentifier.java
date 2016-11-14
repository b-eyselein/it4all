package model;

import model.exercise.ExerciseIdentifier;

public class JsExerciseIdentifier implements ExerciseIdentifier {

  private int id;

  public JsExerciseIdentifier(int theId) {
    id = theId;
  }

  public Integer getId() {
    return id;
  }
  
}