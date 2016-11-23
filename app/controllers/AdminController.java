package controllers;

import controllers.core.UserManagement;
import model.AdminSecured;
import model.feedback.Feedback;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin;
import views.html.evaluation.stats;

@Security.Authenticated(AdminSecured.class)
public class AdminController extends Controller {
  
  public Result index() {
    return ok(admin.render(UserManagement.getCurrentUser()));
  }
  
  public Result stats() {
    return ok(stats.render(UserManagement.getCurrentUser(), Feedback.finder.all()));
  }
  
}
