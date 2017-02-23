package model;

import model.exercise.Exercise;

public class UmlExercise extends Exercise {

<<<<<<< HEAD
  public int diff = 0;
  
  public UmlExercise(int theId) {
    super(theId);
    title = "Der Titel";
    text = "Dies ist der Aufgabentext";
    // TODO Auto-generated constructor stub
=======
  public int id = 1;
  public String exerciseText = "Im folgenden Übungsszenario sollen Sie ein Klassendiagramm in UML erstellen!";
  public String title = "Foto";
  public int diff = 0;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getText() {
    return exerciseText;
>>>>>>> 646d30e9944a9216c6629275935c1c52ea29a88f
  }

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }
  
}