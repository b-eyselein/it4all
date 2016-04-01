package controllers;

import model.user.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import controllers.core.UserControl;

public class Application extends Controller {

  @Security.Authenticated(Secured.class)
  public Result index() {
    return ok(index.render(UserControl.getUser()));
  }
}
