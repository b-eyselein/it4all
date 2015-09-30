package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.php;

public class PHP extends Controller {

  public Result index() {
    return ok(php.render());
  }

}
