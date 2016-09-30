package model.exercise.update;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;

@Entity
@DiscriminatorValue("INSERT")
public class InsertExercise extends SqlExercise {
  
  public InsertExercise(SqlExerciseKey theKey) {
    super(theKey, SqlExerciseType.INSERT);
  }
  
}
