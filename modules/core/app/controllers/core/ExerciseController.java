package controllers.core;

import model.Util;
import play.data.FormFactory;
import play.mvc.Controller;

public class ExerciseController extends Controller {
  
  protected Util util;

  protected FormFactory factory;

  public ExerciseController(Util theUtil, FormFactory theFactory) {
    util = theUtil;
    factory = theFactory;
  }

  protected boolean wantsJsonResponse() {
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }

}
