package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.Util;
import model.WebExercise;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.ElementResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class Web extends ExerciseController {

  private static final String FILE_TYPE = "html";
  private static final String EXERCISE_TYPE = "html";
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";

  public static final String STANDARD_HTML_PLAYGROUND = "<!doctype html>\n<html>\n<head>\n"
      + "<style>\n/* Css-Anweisungen */\n\n</style>\n"
      + "<script type=\"text/javascript\">\n// Javascript-Code\n\n</script>\n"
      + "</head>\n<body>\n  <!-- Html-Elemente -->\n  \n</body>\n</html>";

  @Inject
  public Web(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result correct(int id, String type) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);

    if(!typeIsCorrect(type))
      return badRequest(views.html.error.render(user, "Der Korrekturtyp wurde nicht korrekt spezifiziert!"));

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    List<ElementResult> result = correct(learnerSolution, user, type, exercise);

    log(user, new ExerciseCompletionEvent(request(), id, new LinkedList<>(result)));
    return ok(views.html.correction.render("Web", views.html.webresult.render(result), learnerSolution, getUser()));
  }

  public Result correctLive(int id, String type) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);

    if(!typeIsCorrect(type))
      return badRequest(views.html.error.render(user, "Der Korrekturtyp wurde nicht korrekt spezifiziert!"));

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    List<ElementResult> result = correct(learnerSolution, user, type, exercise);

    log(user, new ExerciseCorrectionEvent(request(), id, new LinkedList<>(result)));
    return ok(views.html.webresult.render(result));
  }

  /**
   * Render exercise with id exercise, use tasks "html" or "css"
   *
   * FIXME: überprüfen, ob nutzer bereits "html" komplett bearbeitet hat!
   *
   * @param exerciseId
   * @param type
   * @return
   */
  public Result exercise(int id, String type) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);

    if(!typeIsCorrect(type) || exercise == null)
      return redirect(controllers.web.routes.Web.index());

    String defaultOrOldSolution = loadDefaultOrOldSolution(id, user);

    log(user, new ExerciseStartEvent(request(), id));

    return ok(views.html.web.render(user, exercise, type, defaultOrOldSolution, "Html-Korrektur"));
  }

  public Result index() {
    return ok(views.html.weboverview.render(getUser(), WebExercise.finder.all()));
  }

  public Result playground() {
    return ok(views.html.playground.render(getUser()));
  }

  private String loadDefaultOrOldSolution(int id, User user) {
    Path oldSolutionPath = util.getSolFileForExercise(user, EXERCISE_TYPE, id, FILE_TYPE);
    if(oldSolutionPath.toFile().exists()) {
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
    if(!solDir.toFile().exists())
      Files.createDirectories(solDir);

    Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  private boolean typeIsCorrect(String type) {
    return "html".equals(type) || "css".equals(type) || "js".equals(type);
  }

  protected List<ElementResult> correct(String learnerSolution, User user, String type, WebExercise exercise) {
    try {
      saveSolutionForUser(user, learnerSolution, exercise.id);
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Html-Loesungsdatei!", error);
      return Collections.emptyList();
    }

    String solutionUrl = "http://localhost:9000" + routes.Solution.site(user, "html", exercise.id).url();

    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);

    return exercise.getTasks(type).stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
  }

}
