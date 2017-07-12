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

  @Column(columnDefinition = "text")
  // FIXME: eventually generate?
  private String fixedStart;

  @Enumerated(EnumType.STRING)
  private XmlExType exerciseType;

  private String referenceFileName;

  public XmlExercise(int theId, String theTitle, String theAuthor, String theText, String theFixedStart,
      XmlExType theExerciseType, String theReferenceFileName) {
    super(theId, theTitle, theAuthor, theText);

    fixedStart = theFixedStart;
    exerciseType = theExerciseType;
    referenceFileName = theReferenceFileName;
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

  public XmlExercise updateValues(int theId, String theTitle, String theAuthor, String theText, String theFixedStart,
      XmlExType theExerciseType, String theReferenceFileName) {
    super.updateValues(theId, theTitle, theAuthor, theText);
    fixedStart = theFixedStart;
    exerciseType = theExerciseType;
    referenceFileName = theReferenceFileName;
    return this;
  }

}