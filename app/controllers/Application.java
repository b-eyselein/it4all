package controllers;

import javax.inject.Inject;

import controllers.core.BaseController;
import model.Secured;
import play.Environment;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Application extends BaseController {
  
  private Environment environment;
  
  @Inject
  public Application(FormFactory theFactory, Environment theEnvironment) {
    super(theFactory);
    environment = theEnvironment;
  }
  
  public Result index() {
    return ok(views.html.index.render(getUser(), environment.isDev()));
  }
  
}
