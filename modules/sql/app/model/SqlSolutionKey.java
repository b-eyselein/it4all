package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import model.exercise.SqlExerciseKey;

@Embeddable
public class SqlSolutionKey implements Serializable {

  private static final long serialVersionUID = 687435168L;

  public String userName;

  public int exerciseId;

  public SqlSolutionKey(String theUsername, int theExerciseId) {
    userName = theUsername;
    exerciseId = theExerciseId;
  }

  public SqlSolutionKey(String theUsername, SqlExerciseKey exerciseKey) {
    userName = theUsername;
    exerciseId = exerciseKey.exerciseId;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof SqlSolutionKey && hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return IntConsts.MILLION * userName.hashCode() + exerciseId;
  }

}
