package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Exercise;
import play.mvc.Http.Request;

public class ExerciseStartEvent extends WorkingEvent {
  
  public ExerciseStartEvent(Request theRequest, Exercise theExercise) {
    super(EventType.EXERCISE_START, theRequest, theExercise);
  }

  @JsonIgnore
  public Exercise getExercise() {
    return exercise;
  }
  
}
