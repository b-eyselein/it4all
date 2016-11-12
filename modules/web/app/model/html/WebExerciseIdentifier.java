package model.html;

import model.exercise.ExerciseIdentifier;

public class WebExerciseIdentifier implements ExerciseIdentifier {

  private int id;
  private String type;
  
  public WebExerciseIdentifier(int theId, String theType) {
    id = theId;
    type = theType;
  }
  
  public int getId() {
    return id;
  }
  
  public String getType() {
    return type;
  }

}
