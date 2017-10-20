package model.logging;

import play.api.mvc.AnyContent;
import play.api.mvc.Request;

public class ExerciseStartEvent extends WorkingEvent {

  public ExerciseStartEvent(Request<AnyContent> theRequest, int id) {
    super(EventType.EXERCISE_START, theRequest, id);
  }

}
