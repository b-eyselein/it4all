package controllers;

import model.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

import javax.inject.Inject;

import controllers.core.UserManagement;
import controllers.web.WebStartUpChecker;

@Security.Authenticated(Secured.class)
public class Application extends Controller {
  
  @Inject
  WebStartUpChecker checker;

  public Result index() {
    return ok(index.render(UserManagement.getCurrentUser()));
  }
}
