package model.css;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.exercise.Exercise;

@Entity
@DiscriminatorValue(value = "css")
public class CssExercise extends Exercise {
  
  public static Finder<Integer, CssExercise> finder = new Finder<Integer, CssExercise>(CssExercise.class);

}
