package model;

import javax.persistence.Entity;

import io.ebean.Finder;
import model.exercise.Exercise;

@Entity
public class SpreadExercise extends Exercise {

  public static final Finder<Integer, SpreadExercise> finder = new Finder<>(SpreadExercise.class);

  public String sampleFilename;

  public String templateFilename;

  public SpreadExercise(int theId) {
    super(theId);
  }

}
