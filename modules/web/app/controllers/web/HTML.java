package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import model.user.User;
import controllers.core.UserManagement;
import model.Util;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.html.result.ElementResult;
import model.user.Secured;
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

public class HTML extends Controller {
  
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String FILE_TYPE = "html";
  private static final String EXERCISE_TYPE = "html";
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";

  @Inject
  Util util;

  @Inject
  WebStartUpChecker checker;

  @Security.Authenticated(Secured.class)
  public Result commit(int exerciseId) {
    User user = UserManagement.getCurrentUser();

    String learnerSolution = extractLearnerSolutionFromRequest(request());
    saveSolutionForUser(user, learnerSolution, exerciseId);

    List<ElementResult> elementResults = correctExercise(user, HtmlExercise.finder.byId(exerciseId));

    if(request().acceptedTypes().get(0).toString().equals("application/json"))
      return ok(Json.toJson(elementResults));
    else
      // TODO: Definitive Abgabe Html, rendere Html!
      return ok(htmlcorrect.render(learnerSolution, elementResults, UserManagement.getCurrentUser()));
  }

  @Security.Authenticated(Secured.class)
  public Result exercise(int exerciseId) {
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.HTML.index() + "\">Startseite</a>.</p>"));
    
    User user = UserManagement.getCurrentUser();

    String defaultOrOldSolution = STANDARD_HTML;
    try {
      Path oldSolutionPath = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exerciseId, FILE_TYPE);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));
      
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    return ok(html.render(user, exercise, defaultOrOldSolution, util.getServerUrl()));
  }

  @Security.Authenticated(Secured.class)
  public Result index() {
    User currentUser = UserManagement.getCurrentUser();

    Path rootFolderForSolutions = util.getRootSolDir();
    if(!Files.exists(rootFolderForSolutions))
      return internalServerError(error.render(currentUser,
          Arrays.asList("Ordner für Lösungen existiert nicht!", "Bitte erstellen Sie diesen Ordner!")));
    
    return ok(htmloverview.render(HtmlExercise.finder.all(), currentUser));
  }

  public Result site(User user, int exercise) {
    Path file = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    if(!Files.exists(file))
      return badRequest("Fehler: Datei nicht vorhanden!");
    
    try {
      return ok(new Html(String.join("\n", Files.readAllLines(file))));
    } catch (IOException error) {
      Logger.error("Fehler beim Lesen einer Html-Datei: " + file, error);
      return badRequest("Fehler beim Lesen der Datei!");
    }

  }

  private List<ElementResult> correctExercise(User user, HtmlExercise exercise) {
    String solutionUrl = routes.HTML.site(user, exercise.id).absoluteURL(request());

    return HtmlCorrector.correct(solutionUrl, exercise, user);
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
