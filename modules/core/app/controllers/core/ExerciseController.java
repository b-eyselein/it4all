package controllers.core;

import model.Util;
import model.exercise.ExerciseIdentifier;
import model.logging.WorkingEvent;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;

public abstract class ExerciseController<I extends ExerciseIdentifier> extends Controller {

  protected Util util;

  protected FormFactory factory;

  public ExerciseController(Util theUtil, FormFactory theFactory) {
    util = theUtil;
    factory = theFactory;
  }

  public static void log(User user, WorkingEvent eventToLog) {
    StringBuilder builder = new StringBuilder();

    // User
    builder.append(user.name + " - " + Json.prettyPrint(Json.toJson(eventToLog)));

    Logger.debug("ToLog:\n" + builder.toString());
  }

  protected abstract CompleteResult correct(Request request, User user, I identifier);

  protected boolean wantsJsonResponse() {
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }

}
