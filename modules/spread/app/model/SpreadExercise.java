package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import model.exercise.Exercise;

@Entity
public class SpreadExercise extends Model implements Exercise {
  
  public static final Finder<Integer, SpreadExercise> finder = new Finder<>(SpreadExercise.class);
  
  @Id
  public int id;
  
  public String title;
  
  @Column(columnDefinition = "text")
  public String text;
  
  public String sampleFilename;

  public String templateFilename;
  
  public SpreadExercise(int theId) {
    id = theId;
  }
  
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
