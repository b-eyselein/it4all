package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import model.ExerciseType;

import com.avaje.ebean.Model;

@Entity
@Table(name = "xmlexercise")
public class XmlExercise extends Model {
  
  public static Finder<Integer, XmlExercise> finder = new Finder<Integer, XmlExercise>(XmlExercise.class);
  
  @Id
  public int id;
  
  public String title;
  
  @Column(name = "exerciseType")
  @Enumerated(EnumType.STRING)
  public ExerciseType exerciseType;
  
  @Column(name = "referenceFileName", length = 100)
  public String referenceFileName; // use with Util.getXmlReferenceFilePath
  
  @Column(name = "exerciseText", length = 1000)
  public String exerciseText;
  
}