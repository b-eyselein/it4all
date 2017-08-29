package model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.ebean.Finder;
import model.exercise.Exercise;

@Entity
public class XmlExercise extends Exercise {
  
  public static final Finder<Integer, XmlExercise> finder = new Finder<>(XmlExercise.class);
  
  // FIXME: eventually generate?
  @Column(columnDefinition = "text")
  @JsonProperty(required = true)
  private String fixedStart;
  
  @Enumerated(EnumType.STRING)
  @JsonProperty(required = true)
  private XmlExType exerciseType;
  
  @JsonProperty(required = true)
  private String referenceFileName;
  
  public XmlExercise(int id) {
    super(id);
  }
  
  public static List<XmlExercise> byType(XmlExType type) {
    return finder.all().stream().filter(ex -> ex.exerciseType == type).collect(Collectors.toList());
  }
  
  public XmlExType getExerciseType() {
    return exerciseType;
  }
  
  public String getFixedStart() {
    return fixedStart.isEmpty() ? "--" : fixedStart;
  }
  
  @JsonProperty("fixedStart")
  public List<String> getFixedStartForJson() {
    return NEW_LINE_SPLITTER.splitToList(fixedStart);
  }
  
  @JsonIgnore
  public String getGrammarFileEnding() {
    return exerciseType.getGramFileEnding();
  }
  
  @JsonIgnore
  public String getReferenceFileEnding() {
    return exerciseType.getRefFileEnding();
  }
  
  public String getReferenceFileName() {
    return referenceFileName;
  }
  
  @JsonIgnore
  public String getStudentFileEnding() {
    return exerciseType.getStudFileEnding();
  }
  
  @JsonIgnore
  public String getTag() {
    return exerciseType.getTag();
  }
  
  public void setExerciseType(XmlExType theExerciseType) {
    exerciseType = theExerciseType;
  }
  
  public void setReferenceFileName(String theReferenceFileName) {
    referenceFileName = theReferenceFileName;
  }
  
  public void setFixedStart(String theFixedStart) {
    fixedStart = theFixedStart;
  }
  
}