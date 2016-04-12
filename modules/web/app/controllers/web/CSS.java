package controllers.web;

import java.util.Arrays;

import controllers.core.UserControl;
import model.user.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.css.cssOverview;

@Security.Authenticated(Secured.class)
public class CSS extends Controller {

  public Result index() {
    return ok(cssOverview.render(Arrays.asList("TEST...", "Test..."), UserControl.getUser()));
  }
}
