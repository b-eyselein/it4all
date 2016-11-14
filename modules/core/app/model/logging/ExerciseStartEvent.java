package model.logging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.exercise.Exercise;
import model.exercise.ExerciseIdentifier;
import play.mvc.Http.Request;

public class ExerciseStartEvent extends WorkingEvent {
  
  private Exercise exercise;

  public ExerciseStartEvent(Request theRequest, Exercise theExercise) {
    super(EventType.EXERCISE_START, theRequest);
    exercise = theExercise;
  }
  
  @JsonIgnore
  public Exercise getExercise() {
    return exercise;
  }

  @Override
  public ExerciseIdentifier getExerciseIdentifier() {
    return exercise.getExerciseIdentifier();
  }
  
}
