package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import com.avaje.ebean.Model;

@Entity
@Table(name = "xmlexercise")
public class XmlExercise extends Model {
  
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

  @Column(name = "exerciseType")
  @Enumerated(EnumType.STRING)
  public XmlExType exerciseType;

  @Column(name = "referenceFileName", length = 100)
  public String referenceFileName; // use with Util.getXmlReferenceFilePath

  @Column(name = "exerciseText", length = 1000)
  public String exerciseText;

}