package controllers.binary;

import controllers.core.UserManagement;
import model.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.overview;

@Security.Authenticated(Secured.class)
public class Binary extends Controller {

  
  public Result index() {
    return ok(overview.render(UserManagement.getCurrentUser()));
  }

}