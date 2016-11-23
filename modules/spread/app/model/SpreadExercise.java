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

  public String title; // NOSONAR

  @Column(columnDefinition = "text")
  public String text;

  public String sampleFilename; // NOSONAR
  
  public String templateFilename; // NOSONAR

  public SpreadExercise(int theId) {
    id = theId;
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
    return text;
  }

  @Override
  public String getTitle() {
    return title;
  }
}
