package controllers;

import controllers.core.UserManagement;
import model.Secured;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user;

@Security.Authenticated(Secured.class)
public class UserController extends Controller {
  
  public Result index() {
    User theUser = UserManagement.getCurrentUser();
    return ok(user.render("User", theUser, theUser.gradings));
  }

}
