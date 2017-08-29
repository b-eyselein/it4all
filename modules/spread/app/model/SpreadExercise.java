package model;

import javax.persistence.Entity;

import io.ebean.Finder;
import model.exercise.Exercise;

@Entity
public class SpreadExercise extends Exercise {
  
  public static final Finder<Integer, SpreadExercise> finder = new Finder<>(SpreadExercise.class);
  
  private String sampleFilename;
  
  private String templateFilename;
  
  public SpreadExercise(int id) {
    super(id);
  }
  
  public String getSampleFilename() {
    return sampleFilename;
  }
  
  public void setSampleFilename(String theSampleFilename) {
    sampleFilename = theSampleFilename;
  }
  
  public String getTemplateFilename() {
    return templateFilename;
  }
  
  public void setTemplateFilename(String theTemplateFilename) {
    templateFilename = theTemplateFilename;
  }
  
}
