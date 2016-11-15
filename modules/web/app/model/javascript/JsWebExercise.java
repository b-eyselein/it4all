package model.javascript;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class JsWebExercise extends Model {

  public static final Finder<Integer, JsWebExercise> finder = new Finder<>(JsWebExercise.class);

  @Id
  public int id;

  public String title; // NOSONAR

  @Column(columnDefinition = "text")
  public String text;
  
  @Column(columnDefinition = "text")
  public String declaration; // NOSONAR

  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsWebTest> tests;

}
