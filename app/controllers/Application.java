package controllers;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import play.Environment;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

@Security.Authenticated(Secured.class)
public class Application extends Controller {
  
  private Environment environment;
  
  @Inject
  public Application(Environment theEnvironment) {
    environment = theEnvironment;
  }

  public Result index() {
    return ok(index.render(UserManagement.getCurrentUser(), environment.isDev()));
  }
  
}
