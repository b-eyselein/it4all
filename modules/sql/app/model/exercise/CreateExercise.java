package model.exercise;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CREATE")
public class CreateExercise extends SqlExercise {
  
  public CreateExercise(SqlExerciseKey theKey) {
    super(theKey, SqlExerciseType.CREATE);
  }
  
}
