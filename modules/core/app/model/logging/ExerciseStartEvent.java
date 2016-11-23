package model.logging;

import model.exercise.ExerciseIdentifier;
import play.mvc.Http.Request;

public class ExerciseStartEvent extends WorkingEvent {
  
  public ExerciseStartEvent(Request theRequest, ExerciseIdentifier<?> theExerciseIdentifier) {
    super(EventType.EXERCISE_START, theRequest, theExerciseIdentifier);
  }
  
}
