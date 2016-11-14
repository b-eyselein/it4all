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

  private EventType eventType;
  private String uri;
  private String method;
  private ZonedDateTime dateTime;

  public WorkingEvent(EventType theEventType, Request theRequest) {
    eventType = theEventType;
    method = theRequest.method();
    uri = theRequest.uri();
    dateTime = ZonedDateTime.now();
  }

  public String getDateTime() {
    return dateTime.format(formatter);
  }

  public EventType getEventType() {
    return eventType;
  }

  public abstract ExerciseIdentifier getExerciseIdentifier();

  public String getMethod() {
    return method;
  }
  
  public String getURI() {
    return uri;
  }

}
