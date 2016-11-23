package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.IntExerciseIdentifier;
import model.Util;
import model.html.result.WebCorrectionResult;
import model.javascript.JsCorrector;
import model.javascript.JsWebExercise;
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
import views.html.javascript.jsoverview;
import views.html.javascript.jsweb;

public class JSWeb extends ExerciseController<IntExerciseIdentifier> {

  private static final String EXERCISE_TYPE = "js";
  private static final String FILE_TYPE = "html";

  @Inject
  public JSWeb(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result commit(IntExerciseIdentifier identifier) {
    User user = UserManagement.getCurrentUser();

    CompleteResult result = correct(request(), user, identifier);

    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), identifier, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), identifier, result));
      return ok(correction.render("Javascript", result, user));
    }
  }

  public Result exercise(IntExerciseIdentifier identifier) {
    User user = UserManagement.getCurrentUser();
    JsWebExercise exercise = JsWebExercise.finder.byId(identifier.id);
    
    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.JSWeb.index() + "\">Startseite</a>.</p>")));
    
    String oldSolution = exercise.declaration;
    try {
      Path file = util.getSolFileForExercise(user, "js", identifier.id, FILE_TYPE);
      if(file.toFile().exists())
        oldSolution = String.join("\n", Files.readAllLines(file));
    } catch (IOException e) {
      Logger.error("Error while loading old JsWeb solution: ", e);
    }
    
    log(user, new ExerciseStartEvent(request(), identifier));
    
    return ok(jsweb.render(user, exercise, identifier, oldSolution));
  }

  public Result index() {
    User user = UserManagement.getCurrentUser();
    return ok(jsoverview.render(user, JsWebExercise.finder.all()));
  }

  private void saveSolutionForUser(User user, String solution, int exercise) throws IOException {
    Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
    if(!solDir.toFile().exists())
      Files.createDirectories(solDir);

    Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  @Override
  protected CompleteResult correct(Request request, User user, IntExerciseIdentifier identifier) {
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");

    JsWebExercise exercise = JsWebExercise.finder.byId(identifier.id);

    try {
      saveSolutionForUser(user, learnerSolution, identifier.id);
    } catch (IOException e) {
      Logger.error("Error while saving file ", e);
      return new WebCorrectionResult(learnerSolution,
          Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Speichern ihrer Lösung!")));
    }

    String solutionUrl = "http://localhost:9000" + routes.Solution.site(user, "js", exercise.id).url();

    List<EvaluationResult> testResults = JsCorrector.correctWeb(exercise, solutionUrl);
    
    return new WebCorrectionResult(learnerSolution, testResults);
  }

}
