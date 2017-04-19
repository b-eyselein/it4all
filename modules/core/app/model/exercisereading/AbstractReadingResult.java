package model.exercisereading;

import model.exercise.Exercise;

public abstract class AbstractReadingResult<T extends Exercise> {
  
  private String json;
  
  public AbstractReadingResult(String theJson) {
    json = theJson;
  }
  
  public String getJson() {
    return json;
  }
  
  public abstract boolean isSuccess();
  
}
