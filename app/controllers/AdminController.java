package controllers;

import controllers.core.UserManagement;
import model.AdminSecured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin;

@Security.Authenticated(AdminSecured.class)
public class AdminController extends Controller {
  
  public Result index() {
    return ok(admin.render(UserManagement.getCurrentUser()));
  }
  
}
