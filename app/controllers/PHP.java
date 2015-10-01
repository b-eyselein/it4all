package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class PHP extends Controller {
  
  public Result index() {
    return redirect("/html");
    // return ok(php.render());
  }
  
}
