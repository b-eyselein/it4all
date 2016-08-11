package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.javascript.JsCorrector;
import model.javascript.JsExercise;
import model.javascript.JsTestResult;
import model.javascript.JsWebExercise;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.error;
import views.html.javascript.js;
import views.html.javascript.jsweb;
import views.html.javascript.jsoverview;
import views.html.javascript.jscorrect;

@Security.Authenticated(Secured.class)
public class JS extends Controller {

  private static final String EXERCISE_TYPE = "js";
  private static final String FILE_TYPE = "html";

  @Inject
  private Util util;

  @Inject
  private FormFactory factory;

  public Result commit(int exerciseId) {
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");

    List<JsTestResult> testResults = JsCorrector.correct(JsExercise.finder.byId(exerciseId), learnerSolution);

    if(request().accepts("application/json"))
      return ok(Json.toJson(testResults));
    else
      // TODO: jscorrect --> Nur für Endkorrektur ?!?
      return ok(jscorrect.render(learnerSolution, testResults, UserManagement.getCurrentUser()));
  }

  public Result commitWeb(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");

    JsWebExercise exercise = new JsWebExercise();

    // FIXME: save only real solution (learnerSolution)?
    String site = exercise.anterior + "\n" + learnerSolution + "\n" + exercise.posterior;
    try {
      saveSolutionForUser(user, site, exerciseId);
    } catch (IOException e) {
      Logger.debug("Error while saving file ", e);
      return badRequest("Error while saving file!");
    }

    String solutionUrl = routes.Solution.site(user, "js", exercise.id).absoluteURL(request());
    String result = JsCorrector.correctWeb(exercise, solutionUrl);

    Logger.debug(result);

    if(request().accepts("application/json"))
      return ok(Json.toJson(result));
    else
      // TODO: jscorrect --> Nur für Endkorrektur ?!?
      return ok("TODO!");
  }

  public Result exercise(int id) {
    User user = UserManagement.getCurrentUser();
    JsExercise exercise = JsExercise.finder.byId(id);

    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.JS.index() + "\">Startseite</a>.</p>")));

    return ok(js.render(UserManagement.getCurrentUser(), exercise));

  }

  public Result exerciseWeb(int id) {
    User user = UserManagement.getCurrentUser();
    return ok(jsweb.render(user, new JsWebExercise()));
  }

  public Result index() {
    User user = UserManagement.getCurrentUser();
    return ok(jsoverview.render(user, JsExercise.finder.all(), Arrays.asList(new JsWebExercise())));
  }

  private void saveSolutionForUser(User user, String solution, int exercise) throws IOException {
    Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
    if(!Files.exists(solDir))
      Files.createDirectories(solDir);

    Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }

}
