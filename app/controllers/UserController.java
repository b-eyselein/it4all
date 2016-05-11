package controllers;

import java.util.List;

import controllers.core.UserControl;
import model.exercise.Grading;
import model.user.Secured;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user;

@Security.Authenticated(Secured.class)
public class UserController extends Controller {
  
  public Result index() {
    User theUser = UserControl.getCurrentUser();
    List<Grading> gradings = Grading.finder.where().eq("student_name", theUser.name).findList();
    return ok(user.render("User", theUser, gradings));
  }

}
