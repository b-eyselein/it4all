package model;

import model.exercise.Exercise;
import model.exercise.ExerciseIdentifier;

public class UmlExercise implements Exercise {

  public int id = 1;
  public String exerciseText = "TODO!";
  public String title = "title";
  
  @Override
  public ExerciseIdentifier getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
  
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
  public String getTitle() {
    return title;
  }
  
}