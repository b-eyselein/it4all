package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;

@Entity
@Table(name = "xmlexercise")
public class XmlExercise extends Model {
  
  public static Finder<Integer, XmlExercise> finder = new Finder<Integer, XmlExercise>(XmlExercise.class);
  
  @Id
  public int id;
  
  public String title;
  
  // type of exercise
  // 0: check XML against XSD
  // 1: check XML against DTD
  // 2: check XSD against XML
  // 3: check DTD against XML
  // TODO: enum?
  @Column(name = "exerciseType")
  public int exerciseType;
  
  @Column(name = "referenceFileName", length = 100)
  public String referenceFileName; // use with Util.getXmlReferenceFilePath
  
  @Column(name = "exerciseText", length = 1000)
  public String exerciseText;
  
  /* @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  public List<Task> tasks; */
  
}