package model;

import javax.persistence.Entity;

import com.fasterxml.jackson.databind.JsonNode;

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
  
  @Override
  public void updateValues(int theId, String theTitle, String theAuthor, String theText, JsonNode exerciseNode) {
    super.updateValues(theId, theTitle, theAuthor, theText);
    
    sampleFilename = exerciseNode.get(StringConsts.SAMPLE_FILENAME).asText();
    templateFilename = exerciseNode.get(StringConsts.TEMPALTE_FILENAME).asText();
  }
  
}
