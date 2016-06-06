package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.exercise.Grading;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.html.result.ElementResult;
import model.user.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Request;
import play.twirl.api.Html;
import views.html.css.*;
import views.html.error;

@Security.Authenticated(Secured.class)
public class CSS extends Controller {
  
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String FILE_TYPE = "html";
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";
  private static final String EXERCISE_TYPE = "html";

  @Inject
  Util util;

  public Result commit(int exerciseId) {
    User user = UserManagement.getCurrentUser();

    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);
    if(exercise == null)
      return badRequest("There is no such exercise!");
    
    String learnerSolution = extractLearnerSolutionFromRequest(request());
    saveSolutionForUser(user, learnerSolution, exerciseId);

    String solutionUrl = routes.HTML.site(user, exercise.id).absoluteURL(request());
    List<ElementResult> elementResults = HtmlCorrector.correctCSS(solutionUrl, HtmlExercise.finder.byId(exerciseId),
        user);

    if(request().accepts("application/json"))
      return ok(Json.toJson(elementResults));
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
      // html, weil anderer Teil der Aufgabe geladen werden soll!
      Path oldSolutionPath = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exerciseId, FILE_TYPE);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));
      
    } catch (IOException e) {
      Logger.error("Fehler beim Laden der alten Lösung!", e);
    }

    return ok(css.render(UserManagement.getCurrentUser(), exercise, defaultOrOldSolution));
  }

  public Result index() {
    List<HtmlExercise> exercises = HtmlExercise.finder.all();
    return ok(cssOverview.render(exercises, UserManagement.getCurrentUser()));
  }

  private String extractLearnerSolutionFromRequest(Request request) {
    return request.body().asFormUrlEncoded().get(LEARNER_SOLUTION_VALUE)[0];
  }

  private void saveSolutionForUser(User user, String solution, int exercise) {
    try {
      Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
      if(!Files.exists(solDir))
        Files.createDirectories(solDir);
      
      Path saveTo = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exercise, FILE_TYPE);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Html-Loesungsdatei!", error);
    }
  }
}
