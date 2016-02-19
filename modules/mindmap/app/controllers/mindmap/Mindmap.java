package controllers.mindmap;

import model.user.Student;
import model.user.User;
import model.user.UserControl;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.mindmapindex;

public class Mindmap extends Controller {
  
  public Result index() {
//    return ok("TODO");
    return ok(mindmapindex.render(UserControl.getUser()));
  }
  
  public Result test() {
    return badRequest("****");
  }
  
}
