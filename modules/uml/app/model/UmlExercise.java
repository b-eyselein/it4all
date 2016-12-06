package model;

import model.exercise.Exercise;

public class UmlExercise extends Exercise {
  
  public int id = 1;
  public String exerciseText = "TODO!";
  
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
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }

}