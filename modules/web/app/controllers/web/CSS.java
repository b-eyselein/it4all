package controllers.web;

import java.util.Arrays;
import java.util.List;

import controllers.core.UserControl;
import controllers.core.Util;
import model.html.HtmlExercise;
import model.html.result.ElementResult;
import model.user.Secured;
import model.user.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.css.cssOverview;
import views.html.css.css;

@Security.Authenticated(Secured.class)
public class CSS extends Controller {
  
  @Security.Authenticated(Secured.class)
  public Result commit(int exerciseId) {
    User user = UserControl.getUser();

    if(request().accepts("application/json"))
      return ok("{}");
    else
      // TODO: Definitive Abgabe Html, rendere Html!
      return ok("TODO!");
  }

  public Result exercise(int id) {
    
    return ok(css.render(UserControl.getUser(), null, Util.getServerUrl()));
  }
  
  public Result index() {
    return ok(cssOverview.render(Arrays.asList("TEST...", "Test..."), UserControl.getUser()));
  }
  
}
