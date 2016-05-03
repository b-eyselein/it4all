package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.html.result.ElementResult;
import model.user.Secured;
import model.user.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.html.html;
import views.html.html.htmloverview;
import controllers.core.UserControl;
import controllers.core.Util;

public class HTML extends Controller {
  
  private static String serverUrl = Util.getServerUrl();
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";

  @Security.Authenticated(Secured.class)
  public Result commit(int exerciseId) {
    User user = UserControl.getUser();

    String learnerSolution = extractLearnerSolutionFromRequest(request());
    saveSolutionForUser(user.getName(), learnerSolution, exerciseId);

    List<ElementResult> elementResults = correctExercise(user, HtmlExercise.finder.byId(exerciseId));

    if(request().accepts("application/json"))
      return ok(Json.toJson(elementResults));
    else
      // TODO: Definitive Abgabe Html, rendere Html!
      return ok("TODO!");
  }

  @Security.Authenticated(Secured.class)
  public Result exercise(int exerciseId) {
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.HTML.index() + "\">Startseite</a>.</p>"));
    
    return ok(html.render(UserControl.getUser(), exercise, serverUrl));
  }

  @Security.Authenticated(Secured.class)
  public Result index() {
    return ok(htmloverview.render(HtmlExercise.finder.all(), UserControl.getUser()));
  }

  public Result site(String userName, int exercise) {
    Path file = Util.getHtmlSolFileForExercise(userName, "html", exercise);
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
    String solutionUrl = routes.HTML.site(user.getName(), exercise.id).absoluteURL(request());

    return HtmlCorrector.correct(solutionUrl, exercise, user);
  }

  private String extractLearnerSolutionFromRequest(Request request) {
    return request.body().asFormUrlEncoded().get(LEARNER_SOLUTION_VALUE)[0];
  }

  private void saveSolutionForUser(String userName, String solution, int exercise) {
    try {
      if(!Files.exists(Util.getSolDirForUser(userName)))
        Files.createDirectory(Util.getSolDirForUser(userName));
      
      Path solDir = Util.getSolDirForUserAndType("html", userName);
      if(!Files.exists(solDir))
        Files.createDirectory(solDir);
      
      Path saveTo = Util.getHtmlSolFileForExercise(userName, "html", exercise);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Html-Loesungsdatei!", error);
    }
  }
}
