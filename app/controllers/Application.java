package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

  public Result index() {
    //TODO: temporary redirect until site is established
    return redirect("/html");
    //return ok(index.render());
  }
}
