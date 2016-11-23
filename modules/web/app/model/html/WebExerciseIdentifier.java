package model.html;

import model.exercise.ExerciseIdentifier;

public class WebExerciseIdentifier implements ExerciseIdentifier<WebExerciseIdentifier> {

  private int id;
  private String type;
  
  public WebExerciseIdentifier(int theId, String theType) {
    id = theId;
    type = theType;
  }
  
  @Override
  public WebExerciseIdentifier bind(String key, String txt) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public int getId() {
    return id;
  }
  
  public String getType() {
    return type;
  }
  
  @Override
  public String javascriptUnbind() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String unbind(String key) {
    // TODO Auto-generated method stub
    return null;
  }

}
