package controllers;

import javax.inject.Inject;

import controllers.core.AController;
import model.Secured;
import model.user.Role;
import model.user.User;
import play.Environment;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Application extends AController {

  private Environment environment;

  @Inject
  public Application(Environment theEnvironment) {
    environment = theEnvironment;

    if(environment.isDev()) {
      // FIXME: create admin user with name "developer" if not exists
      createAdmin("admin");
      createAdmin("developer");
    }

  }
  
  public Result index() {
    return ok(views.html.index.render(getUser(), environment.isDev()));
  }

  private void createAdmin(String name) {
    User admin = User.finder.byId(name);
    if(admin == null)
      admin = new User();
    admin.name = name;
    admin.role = Role.ADMIN;
    admin.save();
  }

}
