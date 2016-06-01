package controllers;

import model.user.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

import javax.inject.Inject;

import controllers.core.UserControl;
import controllers.web.WebStartUpChecker;

public class Application extends Controller {
  
  @Inject
  WebStartUpChecker checker;
  
  @Security.Authenticated(Secured.class)
  public Result index() {
    return ok(index.render(UserControl.getCurrentUser()));
  }
}
