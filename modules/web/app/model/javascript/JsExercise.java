package model.javascript;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;

@Entity
@DiscriminatorValue(value = "js")
public class JsExercise extends Exercise {
  
  public static Finder<Integer, JsExercise> finder = new Finder<Integer, JsExercise>(JsExercise.class);

  public String defaultSolution;

  public String functionName;

  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsTest> functionTests;

  @Override
  public int getMaxPoints() {
    return 2;
  }

}
