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
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.html.html;
import views.html.html.htmlcorrect;
import views.html.html.htmloverview;
import views.html.*;

@Security.Authenticated(Secured.class)
public class HTML extends Controller {

  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String FILE_TYPE = "html";
  private static final String EXERCISE_TYPE = "html";
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";

  @Inject
  private Util util;

  @Inject
  private FormFactory factory;

  @Inject
  @SuppressWarnings("unused")
  private WebStartUpChecker checker;

  public Result commit(int exerciseId, String type) {
    User user = UserManagement.getCurrentUser();
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(error.render(user, new Html("There is no such exercise!")));

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    try {
      saveSolutionForUser(user, learnerSolution, exerciseId);
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Html-Loesungsdatei!", error);
      return badRequest("Es gab einen Fehler beim Speichern der Lösungsdatei!");
    }

    String solutionUrl = "http://localhost:9000" + routes.Solution.site(user, "html", exercise.id).url();

    List<ElementResult> elementResults = Collections.emptyList();

    if(type.equals("html") || type.equals("css"))
      elementResults = HtmlCorrector.correct(solutionUrl, HtmlExercise.finder.byId(exerciseId), user, type);
    else
      return badRequest(error.render(user, new Html("Der Korrekturtyp wurde nicht korrekt spezifiziert!")));

    // Live-Abgabe, sende Resultat als Json
    if(request().acceptedTypes().get(0).toString().equals("application/json"))
      return ok(Json.toJson(elementResults));

    // Definitive Abgabe, zeige Seite
    return ok(htmlcorrect.render(learnerSolution, elementResults, UserManagement.getCurrentUser()));
  }

  public Result exercise(int exerciseId, String type) {
    User user = UserManagement.getCurrentUser();

    if(!type.equals("html") && !type.equals("css"))
      return badRequest(error.render(user, new Html("Der Aufgabentyp wurde nicht korrekt spezifiziert!")));

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
      Path oldSolutionPath = util.getSolFileForExercise(user, EXERCISE_TYPE, exerciseId, FILE_TYPE);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    return ok(html.render(user, exercise, type, defaultOrOldSolution, "Html-Korrektur"));
  }

  public Result index() {
    return ok(htmloverview.render(HtmlExercise.finder.all(), UserManagement.getCurrentUser()));
  }

  private void saveSolutionForUser(User user, String solution, int exercise) throws IOException {
    Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
    if(!Files.exists(solDir))
      Files.createDirectories(solDir);

    Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }
}
