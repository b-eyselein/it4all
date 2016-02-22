package controllers.mindmap;

import model.user.Secured;
import model.user.UserControl;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mindmapindex;
import views.html.mindmapcorrect;

@Security.Authenticated(Secured.class)
public class Mindmap extends Controller {
  
  public Result index() {
    return ok(mindmapindex.render(UserControl.getUser()));
  }
  
  public Result upload() {
    // TODO: getFile, correct and present for download!
    return ok(mindmapcorrect.render(UserControl.getUser()));
  }
  
}
