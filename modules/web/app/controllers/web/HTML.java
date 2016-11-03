package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.exercise.EvaluationResult;
import model.exercise.Grading;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.error;
import views.html.playground;
import views.html.html.html;
import views.html.correction;
import views.html.html.htmlcorrect;
import views.html.html.htmloverview;

@Security.Authenticated(Secured.class)
public class HTML extends ExerciseController {

  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String FILE_TYPE = "html";
  private static final String EXERCISE_TYPE = "html";
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";
  private static final String STANDARD_HTML_PLAYGROUND = "<!doctype html>\n<html>\n<head>\n"
      + "<style>\n/* Css-Anweisungen */\n\n</style>\n"
      + "<script type=\"text/javascript\">\n// Javascript-Code\n\n</script>\n"
      + "</head>\n<body>\n  <!-- Html-Elemente -->\n  \n</body>\n</html>";

  @SuppressWarnings("unused")
  private WebStartUpChecker checker;

  @Inject
  public HTML(Util theUtil, FormFactory theFactory, WebStartUpChecker theChecker) {
    super(theUtil, theFactory);
    checker = theChecker;
  }

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

    if(!typeIsCorrect(type))
      return badRequest(error.render(user, new Html("Der Korrekturtyp wurde nicht korrekt spezifiziert!")));

    List<EvaluationResult> elementResults = HtmlCorrector.correct(solutionUrl, HtmlExercise.finder.byId(exerciseId),
        user, type);

    if(wantsJsonResponse())
      return ok(Json.toJson(elementResults));
    else
      return ok(correction.render("HTML", learnerSolution, elementResults, UserManagement.getCurrentUser()));
  }

  public Result exercise(int exerciseId, String type) {
    User user = UserManagement.getCurrentUser();

    if(!typeIsCorrect(type))
      return badRequest(error.render(user, new Html("Der Aufgabentyp wurde nicht korrekt spezifiziert!")));

    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.HTML.index() + "\">Startseite</a>.</p>")));

    if("css".equals(type) && !Grading.otherPartCompleted(exerciseId, user))
      return badRequest(error.render(user, new Html("Bearbeiten Sie zuerst den <a href=\""
          + routes.HTML.exercise(exerciseId, "html") + "\">Html-Teil der Aufgabe</a> komplett!")));

    String defaultOrOldSolution = loadDefaultOrOldSolution(exerciseId, user);

    return ok(html.render(user, exercise, type, defaultOrOldSolution, "Html-Korrektur"));
  }

  public Result index() {
    return ok(htmloverview.render(HtmlExercise.finder.all(), UserManagement.getCurrentUser()));
  }

  public Result playground() {
    return ok(playground.render(UserManagement.getCurrentUser(), STANDARD_HTML_PLAYGROUND));
  }

  private String loadDefaultOrOldSolution(int exerciseId, User user) {
    Path oldSolutionPath = util.getSolFileForExercise(user, EXERCISE_TYPE, exerciseId, FILE_TYPE);
    if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS)) {
      try {
        return String.join("\n", Files.readAllLines(oldSolutionPath));
      } catch (IOException e) {
        Logger.error("There has been an error loading an old solution for Html:", e);
      }
    }
    return STANDARD_HTML;
  }

  private void saveSolutionForUser(User user, String solution, int exercise) throws IOException {
    Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
    if(!Files.exists(solDir))
      Files.createDirectories(solDir);

    Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  private boolean typeIsCorrect(String type) {
    return "html".equals(type) || "css".equals(type);
  }
}
