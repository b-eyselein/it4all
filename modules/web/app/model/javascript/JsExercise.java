package model.javascript;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class JsExercise extends Model {
  
  public static Finder<Integer, JsExercise> finder = new Finder<>(JsExercise.class);
  
  @Id
  public int id;
  
  public String title;
  
  @Column(columnDefinition = "text")
  public String text;
  
  public String declaration;
  
  public String functionName;
  
  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsTest> functionTests;
  
}
