package model.exercise;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SELECT")
public class SelectExercise extends SqlExercise {
  
  public SelectExercise(SqlExerciseKey theKey) {
    super(theKey, SqlExerciseType.SELECT);
  }
  
}
