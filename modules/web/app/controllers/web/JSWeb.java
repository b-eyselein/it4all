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
import model.Secured;
import model.Util;
import model.html.result.WebCorrectionResult;
import model.javascript.JsCorrector;
import model.javascript.JsWebExercise;
import model.javascript.JsWebExerciseIdentifier;
import model.result.CompleteResult;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.correction;
import views.html.error;
import views.html.javascript.jsoverview;
import views.html.javascript.jsweb;

@Security.Authenticated(Secured.class)
public class JSWeb extends ExerciseController<JsWebExerciseIdentifier> {

  private static final String EXERCISE_TYPE = "js";
  private static final String FILE_TYPE = "html";

  @Inject
  public JSWeb(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result commitWeb(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");

    JsWebExercise exercise = JsWebExercise.finder.byId(exerciseId);

    try {
      saveSolutionForUser(user, learnerSolution, exerciseId);
    } catch (IOException e) {
      Logger.error("Error while saving file ", e);
      return badRequest("Error while saving file!");
    }

    String solutionUrl = "http://localhost:9000" + routes.Solution.site(user, "js", exercise.id).url();

    List<EvaluationResult> testResults = JsCorrector.correctWeb(exercise, solutionUrl);

    if(wantsJsonResponse())
      return ok(Json.toJson(testResults));
    else
      return ok(correction.render("Javascript", learnerSolution, new WebCorrectionResult(learnerSolution, testResults),
          user));
  }

  public Result exerciseWeb(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    JsWebExercise exercise = JsWebExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.JSWeb.index() + "\">Startseite</a>.</p>")));

    String oldSolution = exercise.declaration;
    try {
      Path file = util.getSolFileForExercise(user, "js", exerciseId, FILE_TYPE);
      if(Files.exists(file))
        oldSolution = String.join("\n", Files.readAllLines(file));
    } catch (IOException e) {
      Logger.error("Error while loading old JsWeb solution: ", e);
    }

    return ok(jsweb.render(user, exercise, oldSolution));
  }

  public Result index() {
    User user = UserManagement.getCurrentUser();
    return ok(jsoverview.render(user, JsWebExercise.finder.all()));
  }

  private void saveSolutionForUser(User user, String solution, int exercise) throws IOException {
    Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
    if(!Files.exists(solDir))
      Files.createDirectories(solDir);

    Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  @Override
  protected CompleteResult correct(Request request, User user, JsWebExerciseIdentifier identifier) {
    // TODO Auto-generated method stub
    return null;
  }

}
