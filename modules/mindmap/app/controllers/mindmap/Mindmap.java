package controllers.mindmap;

import controllers.core.UserManagement;
import model.user.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mindmapindex;
import views.html.mindmapcorrect;

@Security.Authenticated(Secured.class)
public class Mindmap extends Controller {
  
  public Result index() {
    return ok(mindmapindex.render(UserManagement.getCurrentUser()));
  }

  public Result upload() {
    // TODO: getFile, correct and present for download!
    return ok(mindmapcorrect.render(UserManagement.getCurrentUser()));
  }

}
