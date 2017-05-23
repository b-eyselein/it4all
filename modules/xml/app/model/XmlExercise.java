package model;

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

  @Column(columnDefinition = "text")
  // FIXME: eventually generate?
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

}