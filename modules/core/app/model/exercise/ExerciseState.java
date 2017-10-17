package model.exercise;

public enum ExerciseState {

  RESERVED, CREATED, APPROVED;

  public String isSelected(ExerciseState that) {
    return this == that ? "selected" : "";
  }

}