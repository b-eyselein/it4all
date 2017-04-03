package controllers;

import javax.inject.Inject;

import model.user.User;
import model.user.Role;

import controllers.core.AController;
import model.Secured;
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
      User admin = User.finder.byId("admin");
      if(admin == null)
        admin = new User();
      admin.name = "admin";
      admin.role = Role.ADMIN;
      admin.save();
    }
    
  }
  
  public Result index() {
    return ok(views.html.index.render(getUser(), environment.isDev()));
  }
  
}
