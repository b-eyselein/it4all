package model.exercise.update;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;

@Entity
@DiscriminatorValue("UPDATE")
public class UpdateExercise extends SqlExercise {
  
  public UpdateExercise(SqlExerciseKey theKey) {
    super(theKey, SqlExerciseType.UPDATE);
  }
  
}
