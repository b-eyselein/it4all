package model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import model.exercise.Exercise;
import play.Logger;
import play.twirl.api.Html;

@Entity
public class XmlExercise extends Exercise {
  
  public enum XmlExType {
    // @formatter:off
    XML_XSD("xml", "xsd", "xsd"),
    XML_DTD("xml", "dtd", "dtd"),
    XSD_XML("xsd", "xml", "xsd"),
    DTD_XML("dtd", "xml", "dtd");
    // @formatter:on
    
    private String studFileEnding;
    private String refFileEnding;
    private String gramFileEnding;
    
    private XmlExType(String studentEnding, String theRefFileEnding, String theGramFileEnding) {
      studFileEnding = studentEnding;
      refFileEnding = theRefFileEnding;
      gramFileEnding = theGramFileEnding;
    }
    
    public String getGramFileEnding() {
      return gramFileEnding;
    }
    
    public String getRefFileEnding() {
      return refFileEnding;
    }
    
    public String getStudFileEnding() {
      return studFileEnding;
    }
    
    public Html getTag() {
      return new Html("<span class=\"label label-default\">" + toString().replace("_", " gegen ") + "</span>");
    }
    
  }
  
  private static final String EX_TYPE = "xml";
  
  public static final Finder<Integer, XmlExercise> finder = new Finder<>(XmlExercise.class);
  
  @Column(columnDefinition = "text")
  public String fixedStart;
  
  @Enumerated(EnumType.STRING)
  public XmlExType exerciseType;
  
  public String referenceFileName; // NOSONAR
  
  public XmlExercise(int theId) {
    super(theId);
  }
  
  public String getGrammarFileEnding() {
    return exerciseType.getGramFileEnding();
  }
  
  public String getReferenceCode(Util util) {
    Path referenceFilePath = getRefFilePath(util);
    
    try {
      if(referenceFilePath.toFile().exists())
        return Util.readFileFromPath(referenceFilePath);
      
    } catch (IOException e) {
      Logger.error("There has been an error reading the file", e);
    }
    return "FEHLER!";
  }
  
  public String getReferenceFileEnding() {
    return exerciseType.getRefFileEnding();
  }
  
  public String getStudentFileEnding() {
    return exerciseType.getStudFileEnding();
  }
  
  public Html getTag() {
    return exerciseType.getTag();
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-6\">");
    builder.append("<div class=\"panel panel-default\">");
    builder.append("<div class=\"panel-heading\">Aufgabe " + id + ": " + title + DIV_END);
    builder.append("<div class=\"panel-body\">");
    builder.append("<p>Aufgabentext: " + text + "</p>");
    builder.append("<p>Aufgabentyp: " + exerciseType + "</p>");
    builder.append("<p>Dateiname: &quot;" + referenceFileName + "&quot;</p>");
    builder.append("<p>Fixed Start: &quot;" + fixedStart + "&quot;</p>");
    builder.append(DIV_END + DIV_END + DIV_END);
    return builder.toString();
  }
  
  private Path getRefFilePath(Util util) {
    // @formatter:off
    return Paths
        .get(util.getSampleFileForExercise(EX_TYPE, referenceFileName).toString() + "." + getReferenceFileEnding());
    // @formatter:on
  }
  
}