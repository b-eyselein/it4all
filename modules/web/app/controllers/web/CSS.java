package controllers.web;

import controllers.core.UserControl;
import controllers.core.Util;
import java.util.List;

import javax.inject.Inject;

import model.css.CssExercise;
import model.user.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.css.css;
import views.html.css.cssOverview;

@Security.Authenticated(Secured.class)
public class CSS extends Controller {
  
  @Inject
  Util util;

  @Security.Authenticated(Secured.class)
  public Result commit(int exerciseId) {
    if(request().accepts("application/json"))
      return ok("{}");
    else
      // TODO: Definitive Abgabe Html, rendere Html!
      return ok("TODO!");
  }

  public Result exercise(int id) {
    
    CssExercise exercise = CssExercise.finder.byId(id);

    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.HTML.index() + "\">Startseite</a>.</p>"));
    
    return ok(css.render(UserControl.getCurrentUser(), exercise, util.getServerUrl()));
  }

  public Result index() {
    List<CssExercise> exercises = CssExercise.finder.all();
    return ok(cssOverview.render(exercises, UserControl.getCurrentUser()));
  }

}
