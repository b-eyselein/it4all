package model;

import javax.persistence.Entity;

import io.ebean.Finder;
import model.exercise.Exercise;

@Entity
public class SpreadExercise extends Exercise {
  
  public static final Finder<Integer, SpreadExercise> finder = new Finder<>(SpreadExercise.class);
  
  public String sampleFilename;
  
  public String templateFilename;
  
  public SpreadExercise(int theId, String theTitle, String theAuthor, String theText, String theSampleFilename,
      String theTemplateFilename) {
    super(theId, theTitle, theAuthor, theText);
    sampleFilename = theSampleFilename;
    templateFilename = theTemplateFilename;
  }
  
}
