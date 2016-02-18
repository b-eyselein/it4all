package controllers.mindmap;

import play.mvc.Controller;
import play.mvc.Result;

public class Mindmap extends Controller {
  
  public Result index() {
    return ok("Test");
  }
  
  public Result test() {
    return badRequest("****");
  }
  
}
