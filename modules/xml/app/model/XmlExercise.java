package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import model.exercise.Exercise;

@Entity
@Table(name = "xmlexercise")
public class XmlExercise extends Model implements Exercise {
  
  public enum XmlExType {
    XML_XSD("xml"), XML_DTD("xml"), XSD_XML("xsd"), DTD_XML("dtd");

    private String studentFileEnding;

    private XmlExType(String studentEnding) {
      this.studentFileEnding = studentEnding;
    }
    
    public String getFileEnding() {
      return studentFileEnding;
    }
  }
  
  public static final Finder<Integer, XmlExercise> finder = new Finder<>(XmlExercise.class);

  @Id
  public int id;

  public String title; // NOSONAR

  @Column(columnDefinition = "text")
  public String fixedStart;

  @Enumerated(EnumType.STRING)
  public XmlExType exerciseType;

  @Column(name = "referenceFileName", length = 100)
  public String referenceFileName; // use with Util.getXmlReferenceFilePath

  @Column(name = "exerciseText", length = 1000)
  public String exerciseText;

  @Override
  public String getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
  
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
    return exerciseText;
  }
  
  @Override
  public String getTitle() {
    return title;
  }

}