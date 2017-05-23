package controllers;

import javax.inject.Inject;

import controllers.core.AbstractController;
import model.Secured;
import play.Environment;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Application extends AbstractController {

  private Environment environment;

  @Inject
  public Application(FormFactory theFactory, Environment theEnvironment) {
    super(theFactory, "app");
    environment = theEnvironment;
  }

  public Result index() {
    return ok(views.html.index.render(getUser(), environment.isDev()));
  }

}
