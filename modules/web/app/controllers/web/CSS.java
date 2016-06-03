package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.exercise.Grading;
import model.html.HtmlExercise;
import model.user.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.css.*;
import views.html.error;

@Security.Authenticated(Secured.class)
public class CSS extends Controller {
  
  private static final String FILE_TYPE = "html";
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";
  private static final String EXERCISE_TYPE = "html";
  @Inject
  Util util;
  
  public Result commit(int exerciseId) {
    if(request().accepts("application/json"))
      return ok("{}");
    else
      // TODO: Definitive Abgabe Html, rendere Html!
      return ok("TODO!");
  }
  
  public Result exercise(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);
    
    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.HTML.index() + "\">Startseite</a>.</p>")));
    
    // check grading to see if user has done html part of this exercise
    // completely
    List<Grading> gradings = Grading.finder.where().eq("user_name", user.name).eq("exercise_id", exerciseId).findList();
    if(gradings.size() == 0 || !gradings.get(0).hasAllPoints())
      return badRequest(error.render(user, new Html("Bearbeiten Sie zuerst den <a href=\""
          + routes.HTML.exercise(exerciseId) + "\">Html-Teil der Aufgabe</a> komplett!")));
    
    String defaultOrOldSolution = STANDARD_HTML;
    try {
      Path oldSolutionPath = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exerciseId, FILE_TYPE);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));
      
    } catch (IOException e) {
      Logger.error("Fehler beim Laden der alten Lösung!", e);
    }
    
    return ok(css.render(UserManagement.getCurrentUser(), exercise, defaultOrOldSolution, util.getServerUrl()));
  }
  
  public Result index() {
    List<HtmlExercise> exercises = HtmlExercise.finder.all();
    return ok(cssOverview.render(exercises, UserManagement.getCurrentUser()));
  }
  
}
