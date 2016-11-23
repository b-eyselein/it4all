package model;

import model.exercise.ExerciseIdentifier;

public class IntExerciseIdentifier implements ExerciseIdentifier<IntExerciseIdentifier> {
  
  public int id; // NOSONAR
  
  public IntExerciseIdentifier() {
    id = 0;
  }
  
  public IntExerciseIdentifier(int theId) {
    id = theId;
  }
  
  @Override
  public IntExerciseIdentifier bind(String key, String txt) {
    return new IntExerciseIdentifier(Integer.parseInt(txt));
  }
  
  @Override
  public String javascriptUnbind() {
    return "function(k,v) {\n  return v.id;\n}";
  }
  
  @Override
  public String unbind(String key) {
    return Integer.toString(id);
  }
  
}