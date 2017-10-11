package controllers;

import controllers.core.BaseController;
import model.Secured;
import play.Environment;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

@Security.Authenticated(Secured.class) public class Application extends BaseController {

  private Environment environment;

  @Inject public Application(FormFactory f, Environment theEnvironment) {
    super(f);
    environment = theEnvironment;
  }

  public Result index() {
    return ok(views.html.index.render(getUser(), environment.isDev()));
  }

}
