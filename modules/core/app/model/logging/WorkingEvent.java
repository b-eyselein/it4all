package model.logging;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import model.exercise.ExerciseIdentifier;
import play.mvc.Http.Request;

public abstract class WorkingEvent {
  
  public enum EventType {
    EXERCISE_START, EXERCISE_CORRECTION, EXERCISE_COMPLETION;
  }
  
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss Z");
  
  protected EventType eventType;
  protected String uri;
  protected String method;
  protected ZonedDateTime dateTime;
  protected ExerciseIdentifier<?> exerciseIdentifier;
  
  public WorkingEvent(EventType theEventType, Request theRequest, ExerciseIdentifier<?> theExerciseIdentifier) {
    eventType = theEventType;
    method = theRequest.method();
    uri = theRequest.uri();
    dateTime = ZonedDateTime.now();
    exerciseIdentifier = theExerciseIdentifier;
  }
  
  public String getDateTime() {
    return dateTime.format(formatter);
  }
  
  public EventType getEventType() {
    return eventType;
  }
  
  public ExerciseIdentifier<?> getExerciseIdentifier() {
    return exerciseIdentifier;
  }
  
  public String getMethod() {
    return method;
  }

  public String getURI() {
    return uri;
  }
  
}
