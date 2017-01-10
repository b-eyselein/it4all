package model;

import model.exercise.Exercise;

public class UmlExercise extends Exercise {

  public int diff = 0;
  
  public UmlExercise(int theId) {
    super(theId);
    title = "Der Titel";
    text = "Dies ist der Aufgabentext";
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }
  
}