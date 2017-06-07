package model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Finder;
import model.exercise.Exercise;

@Entity
public class XmlExercise extends Exercise {

  public static final Finder<Integer, XmlExercise> finder = new Finder<>(XmlExercise.class);

  @Column(columnDefinition = "text")
  // FIXME: eventually generate?
  public String fixedStart;

  @Enumerated(EnumType.STRING)
  public XmlExType exerciseType;

  public String referenceFileName;

  public XmlExercise(int theId) {
    super(theId);
  }

  public static List<XmlExercise> byType(XmlExType type) {
    return finder.all().stream().filter(ex -> ex.exerciseType == type).collect(Collectors.toList());
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