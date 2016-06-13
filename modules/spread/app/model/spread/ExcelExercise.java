package model.spread;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.exercise.Exercise;

@Entity
@DiscriminatorValue(value = "spread")
public class ExcelExercise extends Exercise {
  
  public static Finder<Integer, ExcelExercise> finder = new Finder<Integer, ExcelExercise>(ExcelExercise.class);

  public String fileName;

  @Override
  public int getMaxPoints() {
    return 1;
  }

}
