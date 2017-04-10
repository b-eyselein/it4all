package model.result;

import model.UmlExercise;

public abstract class UmlResult {

  private UmlExercise exercise;

  public UmlResult(UmlExercise theExercise) {
    exercise = theExercise;
  }

  public UmlExercise getExercise() {
    return exercise;
  }

}
