package model.logging;

import play.mvc.Http.Request;

public class ExerciseStartEvent extends WorkingEvent {

  public ExerciseStartEvent(Request theRequest, int id) {
    super(EventType.EXERCISE_START, theRequest, id);
  }

}
