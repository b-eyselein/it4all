package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class SQL extends Controller {
  
  public Result index() {
    return redirect("/html");
    // return ok(sql.render());
  }
  
}