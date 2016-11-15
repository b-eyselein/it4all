package model;

import javax.persistence.Id;

import com.avaje.ebean.Model;

import model.exercise.Exercise;

public class PythonExercise extends Model implements Exercise {
  
  @Id
  public int id;

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
