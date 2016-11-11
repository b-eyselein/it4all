package controllers.core;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.Util;
import model.exercise.Exercise;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;

public class ExerciseController extends Controller {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss Z");
  protected Util util;

  protected FormFactory factory;

  public ExerciseController(Util theUtil, FormFactory theFactory) {
    util = theUtil;
    factory = theFactory;
  }

  public static void log(User user, Request request, List<String> toLog) {
    ZonedDateTime dateTime = ZonedDateTime.now();

    StringBuilder builder = new StringBuilder();

    builder.append("{");

    // User
    builder.append("\"user\": \"" + user.name + "\", ");

    // Date and time
    builder.append("\"dateTime\": \"" + dateTime.format(formatter) + "\", ");

    // Method and URL
    builder.append("\"call\": {\"method\": \"" + request.method() + "\", \"url\": \"" + request.uri() + "\"}, ");

    // Event
    builder
        .append("\"event\": [" + toLog.stream().map(toL -> "\"" + toL + "\"").collect(Collectors.joining(", ")) + "]");

    builder.append("}");

    Logger.debug("ToLog:\n>>" + builder.toString() + "<<");

    Logger.debug("As JSON:\n" + Json.prettyPrint(Json.parse(builder.toString())));

  }

  public static void logExerciseCorrection(User user, List<EvaluationResult> elementResults) {
    log(user, request(), Arrays.asList("TODO!"));
  }

  public static void logExerciseStart(User user, Exercise exercise) {
    log(user, request(), Arrays.asList("Starting exercise " + exercise.getId()));
  }

  protected boolean wantsJsonResponse() {
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }

}
