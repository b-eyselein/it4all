package model;

import model.exercise.ExerciseIdentifier;

public class XmlExerciseIdentifier implements ExerciseIdentifier {
  
  private int id;
  
  public XmlExerciseIdentifier(int theId) {
    id = theId;
  }
  
  public Integer getId() {
    return id;
  }

}
