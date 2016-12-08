package model.exercise;

import play.mvc.PathBindable;

public enum SqlExerciseType implements PathBindable<SqlExerciseType> {
  
  SELECT, CREATE, UPDATE, DELETE, INSERT;

  @Override
  public SqlExerciseType bind(String key, String txt) {
    return SqlExerciseType.valueOf(txt);
  }

  @Override
  public String javascriptUnbind() {
    // FIXME Auto-generated method stub
    return null;
  }

  @Override
  public String unbind(String key) {
    // FIXME Auto-generated method stub
    return null;
  }
}
