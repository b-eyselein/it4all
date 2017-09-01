package model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
  private XmlExType exerciseType;
  
  @JsonProperty(required = true)
  private String rootNode;
  
  public static List<XmlExercise> byType(XmlExType type) {
    return finder.all().stream().filter(ex -> ex.exerciseType == type).collect(Collectors.toList());
  }
  
  public XmlExercise(int id) {
    super(id);
    exerciseType = XmlExType.XML_DTD;
    rootNode = "";
  }
  
  public XmlExType getExerciseType() {
    return exerciseType;
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
  
  @Override
  public List<String> getRestHeaders() {
    return Arrays.asList("Typ", "Wurzelknoten");
  }

  public String getRootNode() {
    return rootNode;
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
    return views.html.xmlAdmin.xmlExRest.render(this);
  }
  
  public void setExerciseType(XmlExType theExerciseType) {
    exerciseType = theExerciseType;
  }
  
  public void setRootNode(String theRootNode) {
    rootNode = theRootNode;
  }
  
}