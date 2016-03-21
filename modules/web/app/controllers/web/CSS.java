package controllers.web;

import model.user.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class CSS extends Controller {
  
  public Result index() {
    return ok("TEST...");
  }
  
}
