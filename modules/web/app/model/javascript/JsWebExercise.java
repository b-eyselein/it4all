package model.javascript;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;

@Entity
public class JsWebExercise extends Exercise {
  
  public static final Finder<Integer, JsWebExercise> finder = new Finder<>(JsWebExercise.class);
  
  @Id
  public int id;
  
  @Column(columnDefinition = "text")
  public String text;

  @Column(columnDefinition = "text")
  public String declaration; // NOSONAR
  
  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsWebTest> tests;
  
  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }
  
}
