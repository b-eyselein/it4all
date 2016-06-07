package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import model.user.User;
import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.exercise.Grading;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.html.result.ElementResult;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.html.html;
import views.html.html.htmlcorrect;
import views.html.html.htmloverview;
import views.html.*;
import views.html.css.css;

@Security.Authenticated(Secured.class)
public class HTML extends Controller {
  
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String FILE_TYPE = "html";
  private static final String EXERCISE_TYPE = "html";
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";

  @Inject
  Util util;

  @Inject
  WebStartUpChecker checker;

  public Result commit(int exerciseId, String type) {
    User user = UserManagement.getCurrentUser();
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(error.render(user, new Html("There is no such exercise!")));
    
    String learnerSolution = extractLearnerSolutionFromRequest(request());
    saveSolutionForUser(user, learnerSolution, exerciseId);

    String solutionUrl = routes.Solution.site(user, exercise.id).absoluteURL(request());

    List<ElementResult> elementResults = Collections.emptyList();

    if(type.equals("html"))
      elementResults = HtmlCorrector.correctHtml(solutionUrl, HtmlExercise.finder.byId(exerciseId), user);
    else if(type.equals("css"))
      elementResults = HtmlCorrector.correctCSS(solutionUrl, HtmlExercise.finder.byId(exerciseId), user);
    else
      return badRequest(error.render(user, new Html("Der Korrekturtyp wurde nicht korrekt spezifiziert!")));
    
    if(request().acceptedTypes().get(0).toString().equals("application/json"))
      return ok(Json.toJson(elementResults));
    else if(type.equals("html"))
      // TODO: Definitive Abgabe Html, rendere Html!
      return ok(htmlcorrect.render(learnerSolution, elementResults, UserManagement.getCurrentUser()));
    else
      // TODO: Definitive Abgabe Css, rendere Html!
      return ok("TODO!");
  }

  public Result exercise(int exerciseId, String type) {
    User user = UserManagement.getCurrentUser();
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.HTML.index() + "\">Startseite</a>.</p>")));
    
    if(type.equals("css") && !Grading.otherPartCompleted(exerciseId, user))
      return badRequest(error.render(user, new Html("Bearbeiten Sie zuerst den <a href=\""
          + routes.HTML.exercise(exerciseId, "html") + "\">Html-Teil der Aufgabe</a> komplett!")));
    
    String defaultOrOldSolution = STANDARD_HTML;
    try {
      Path oldSolutionPath = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exerciseId, FILE_TYPE);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    if(type.equals("html"))
      return ok(html.render(user, exercise, defaultOrOldSolution));
    else if(type.equals("css"))
      return ok(css.render(UserManagement.getCurrentUser(), exercise, defaultOrOldSolution));
    else
      return badRequest(error.render(user, new Html("Der Aufgabentyp wurde nicht korrekt spezifiziert!")));
    
  }

  public Result index() {
    User currentUser = UserManagement.getCurrentUser();

    Path rootFolderForSolutions = util.getRootSolDir();
    if(!Files.exists(rootFolderForSolutions))
      return internalServerError(error.render(currentUser,
          new Html("<p>Ordner für Lösungen existiert nicht!</p><p>Bitte erstellen Sie diesen Ordner!</p>")));
    
    return ok(htmloverview.render(HtmlExercise.finder.all(), currentUser));
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
