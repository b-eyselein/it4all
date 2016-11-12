package model;

import model.exercise.Exercise;

public class UmlExercise implements Exercise {

  public int id = 1;
  public String exerciseText = "TODO!";
  public String title = "title";
  
  @Override
  public String getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String getTitle() {
    // TODO Auto-generated method stub
    return null;
  }
  
}