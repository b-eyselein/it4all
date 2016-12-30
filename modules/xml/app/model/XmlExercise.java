package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import model.exercise.Exercise;
import play.twirl.api.Html;

@Entity
public class XmlExercise extends Exercise {
  
  public enum XmlExType {
    XML_XSD("xml"), XML_DTD("xml"), XSD_XML("xsd"), DTD_XML("dtd");
    
    private String studentFileEnding;
    
    private XmlExType(String studentEnding) {
      this.studentFileEnding = studentEnding;
    }
    
    public String getFileEnding() {
      return studentFileEnding;
    }
    
    public Html getTag() {
      return new Html("<span class=\"label label-default\">" + toString().replace("_", " gegen ") + "</span>");
    }
    
  }
  
  public static final Finder<Integer, XmlExercise> finder = new Finder<>(XmlExercise.class);
  
  @Id
  public int id;
  
  @Column(columnDefinition = "text")
  public String fixedStart;
  
  @Enumerated(EnumType.STRING)
  public XmlExType exerciseType;
  
  public String referenceFileName; // NOSONAR
  
  public String text; // NOSONAR
  
  public String getGrammarFileEnding() {
    switch(exerciseType) {
    case DTD_XML:
    case XML_DTD:
      return ".dtd";
    case XML_XSD:
    case XSD_XML:
      return ".xsd";
    default:
      return null;
    }
  }
  
  @Override
  public int getId() {
    return id;
  }
  
  public Html getTag() {
    return exerciseType.getTag();
  }
  
  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public String getReferenceFileEnding() {
    switch(exerciseType) {
    case DTD_XML:
    case XSD_XML:
      return ".xml";
    case XML_DTD:
      return ".dtd";
    case XML_XSD:
      return ".xsd";
    default:
      return null;
    }
  }
  
  @Override
  public String getText() {
    return text;
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-6\">");
    builder.append("<div class=\"panel panel-default\">");
    builder.append("<div class=\"panel-heading\">Aufgabe " + getId() + ": " + getTitle() + DIV_END);
    builder.append("<div class=\"panel-body\">");
    builder.append("<p>Aufgabentext: " + text + "</p>");
    builder.append("<p>Aufgabentyp: " + exerciseType + "</p>");
    builder.append("<p>Dateiname: &quot;" + referenceFileName + "&quot;</p>");
    builder.append("<p>Fixed Start: &quot;" + fixedStart + "&quot;</p>");
    builder.append(DIV_END + DIV_END + DIV_END);
    return builder.toString();
  }
  
}