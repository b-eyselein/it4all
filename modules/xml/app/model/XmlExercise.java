package model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Exercise;

@Entity
public class XmlExercise extends Exercise {
  
  public static final com.avaje.ebean.Model.Finder<Integer, XmlExercise> finder = new com.avaje.ebean.Model.Finder<>(
      XmlExercise.class);
  
  private static final XmlExerciseReader READER = new XmlExerciseReader();
  
  @Column(columnDefinition = "text")
  public String fixedStart;
  
  @Enumerated(EnumType.STRING)
  public XmlExType exerciseType;
  
  public String referenceFileName;
  
  public XmlExercise(int theId) {
    super(theId);
  }
  
  public List<String> getFixedStart() {
    return NEW_LINE_SPLITTER.splitToList(fixedStart);
  }
  
  @JsonIgnore
  public String getGrammarFileEnding() {
    return exerciseType.getGramFileEnding();
  }
  
  public String getReferenceCode() {
    Path referenceFilePath = Paths.get(READER.baseTargetDir.toString(),
        referenceFileName + "." + getReferenceFileEnding());
    
    if(referenceFilePath.toFile().exists())
      return XmlExerciseReader.readFile(referenceFilePath);
    
    return "FEHLER!";
  }
  
  @JsonIgnore
  public String getReferenceFileEnding() {
    return exerciseType.getRefFileEnding();
  }
  
  @JsonIgnore
  public String getStudentFileEnding() {
    return exerciseType.getStudFileEnding();
  }
  
  @JsonIgnore
  public String getTag() {
    return exerciseType.getTag();
  }
  
  @Override
  public void saveInDB() {
    save();
    READER.checkOrCreateSampleFile(this);
  }
  
}