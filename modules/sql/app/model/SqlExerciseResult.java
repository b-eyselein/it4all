package model;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;

import model.exercise.SqlExercise;
import model.user.ExerciseResult;
import model.user.User;

@DiscriminatorValue(value = "sql")
public class SqlExerciseResult extends ExerciseResult<SqlExercise> {
  
  @Embeddable
  public static class SqlExerciseResultKey implements Serializable {

    private static final long serialVersionUID = -4662606862958091795L;
    
    public String username;

    public int exerciseId;

  }

  public boolean done; // NOSONAR
  
  public SqlExerciseResult(User theUser, SqlExercise theExercise) {
    user = theUser;
    exercise = theExercise;
  }

}
