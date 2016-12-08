package controllers;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.user.Role;
import model.user.User;
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

    if(environment.isDev()) {
      User admin = User.finder.byId("admin");
      if(admin == null)
        admin = new User();
      admin.name = "admin";
      admin.role = Role.ADMIN;
      admin.save();
    }

  }

  public Result index() {
    return ok(index.render(UserManagement.getCurrentUser(), environment.isDev()));
  }

}
