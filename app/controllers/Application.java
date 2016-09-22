package controllers;

import controllers.core.UserManagement;
import model.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

@Security.Authenticated(Secured.class)
public class Application extends Controller {

  public Result index() {
    return ok(index.render(UserManagement.getCurrentUser()));
  }

}
