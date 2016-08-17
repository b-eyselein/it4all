package model.javascript.web;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class JsWebExercise extends Model {

  public static Finder<Integer, JsWebExercise> finder = new Finder<>(JsWebExercise.class);

  @Id
  public int id;

  public String title;

  @Column(columnDefinition = "text")
  public String text;
  
  @Column(columnDefinition = "text")
  public String anterior;
  
  @Column(columnDefinition = "text")
  public String posterior;

  public String declaration;

  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsWebTest> tests;

}
