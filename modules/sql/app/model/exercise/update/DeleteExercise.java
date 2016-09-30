package model.exercise.update;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;

@Entity
@DiscriminatorValue("DELETE")
public class DeleteExercise extends SqlExercise {
  
  public DeleteExercise(SqlExerciseKey theKey) {
    super(theKey, SqlExerciseType.DELETE);
  }
  
}
