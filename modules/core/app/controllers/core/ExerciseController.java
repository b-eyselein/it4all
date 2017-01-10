package controllers.core;

import model.Secured;
import model.Util;
import model.logging.WorkingEvent;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Security.Authenticated;

@Authenticated(Secured.class)
public abstract class ExerciseController extends Controller {
  
  private static final Logger.ALogger PROGRESS_LOGGER = Logger.of("progress");
  
  protected Util util;
  
  protected FormFactory factory;
  
  public ExerciseController(Util theUtil, FormFactory theFactory) {
    util = theUtil;
    factory = theFactory;
  }
  
  protected static void log(User user, WorkingEvent eventToLog) {
    PROGRESS_LOGGER.debug(user.name + " - " + Json.toJson(eventToLog));
  }
  
  protected abstract CompleteResult correct(Request request, User user, int id);
  
  protected boolean wantsJsonResponse() {
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }
  
}
