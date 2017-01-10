package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.Util;
import model.WebExercise;
import model.WebExerciseIdentifier;
import model.task.Task;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.CompleteResult;
import model.result.EvaluationFailed;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.correction;
import views.html.error;
import views.html.playground;
import views.html.web;
import views.html.weboverview;

public class Web extends ExerciseController<WebExerciseIdentifier> {
  
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
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
  
  public Result commit(int exerciseId, String type) {
    User user = UserManagement.getCurrentUser();
    
    if(!typeIsCorrect(type))
      return badRequest(error.render(user, new Html("Der Korrekturtyp wurde nicht korrekt spezifiziert!")));
    
    WebExerciseIdentifier identifier = new WebExerciseIdentifier(exerciseId, type);
    CompleteResult result = correct(request(), user, identifier);
    
    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), identifier, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), identifier, result));
      return ok(correction.render("Web", result, UserManagement.getCurrentUser()));
    }
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
  public Result exercise(int exerciseId, String type) {
    User user = UserManagement.getCurrentUser();
    WebExercise exercise = WebExercise.finder.byId(exerciseId);
    
    if(!typeIsCorrect(type) || exercise == null)
      return redirect(controllers.web.routes.Web.index());
    
    String defaultOrOldSolution = loadDefaultOrOldSolution(exerciseId, user);
    
    log(user, new ExerciseStartEvent(request(), new WebExerciseIdentifier(exerciseId, type)));
    
    return ok(web.render(user, exercise, type, defaultOrOldSolution, "Html-Korrektur"));
  }
  
  public Result index() {
    return ok(weboverview.render(UserManagement.getCurrentUser(), WebExercise.finder.all()));
  }
  
  public Result playground() {
    return ok(playground.render(UserManagement.getCurrentUser()));
  }
  
  private String loadDefaultOrOldSolution(int exerciseId, User user) {
    Path oldSolutionPath = util.getSolFileForExercise(user, EXERCISE_TYPE, exerciseId, FILE_TYPE);
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
  
  @Override
  protected CompleteResult correct(Request request, User user, WebExerciseIdentifier id) {
    WebExercise exercise = WebExercise.finder.byId(id.getId());
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    try {
      saveSolutionForUser(user, learnerSolution, exercise.id);
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Html-Loesungsdatei!", error);
      return new CompleteResult(learnerSolution, Arrays.asList(new EvaluationFailed(
          "Es gab einen Fehler beim Speichern der Lösungsdatei. Daher konnte die Korrektur nicht durchgeführt werden!")));
    }
    
    String solutionUrl = "http://localhost:9000" + routes.Solution.site(user, "html", exercise.id).url();
    
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    
    List<? extends Task> tasks = exercise.getTasks(id.getType());
    
    List<EvaluationResult> results = tasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
    
    // if("html".equals(type))
    // saveGrading(exercise, student, calculatePoints(results));
    
    return new CompleteResult(learnerSolution, results);
  }
  
  // private static int calculatePoints(List<EvaluationResult> result) {
  // return result.stream().mapToInt(EvaluationResult::getPoints).sum();
  // }
  
  // private static void saveGrading(WebExercise exercise, User user, int
  // points) {
  // GradingKey gradingKey = new GradingKey(user.name, exercise.id);
  // Grading grading = Grading.finder.byId(gradingKey);
  //
  // if(grading == null)
  // grading = new Grading(gradingKey);
  //
  // grading.setPoints(points);
  // grading.save();
  // }
  
}
