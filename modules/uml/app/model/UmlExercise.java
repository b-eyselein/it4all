package model;

import model.exercise.Exercise;

public class UmlExercise extends Exercise {
  
  public UmlExercise(int theId) {
    super(theId);
    id = 1;
    text = "Im folgenden Übungsszenario sollen Sie ein Klassendiagramm in UML erstellen!";
    title = "Foto";
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }
  
}