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
import play.twirl.api.Html;

@Entity
public class XmlExercise extends Exercise {

  public static final Finder<Integer, XmlExercise> finder = new Finder<>(XmlExercise.class);

  @Enumerated(EnumType.STRING)
  @JsonProperty(required = true)
  public XmlExType exerciseType;

  @Column
  @JsonProperty(required = true)
  public String rootNode;

  public XmlExercise(int id) {
    super(id);
  }

  public static List<XmlExercise> byType(XmlExType type) {
    return finder.all().stream().filter(ex -> ex.exerciseType == type).collect(Collectors.toList());
  }

  public String getFixedStart() {
    if(exerciseType != XmlExType.XML_DTD)
      return "";

    return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n<!DOCTYPE %s SYSTEM \"%s.dtd\">", rootNode,
        rootNode);
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

  @Override
  @JsonIgnore
  public Html renderRest() {
    return new Html("<td>" + exerciseType + "</td>\n<td>" + rootNode + "</td>");
  }

}