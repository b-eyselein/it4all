package model;

import model.exercise.Exercise;

public class UmlExercise extends Exercise {

  public int id = 1;

  public String exerciseText = "Im folgenden Übungsszenario sollen Sie ein Klassendiagramm in UML erstellen!";
  public String title = "Foto";
  public int diff = 0;

  public UmlExercise(int theId) {
    super(theId);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }

}