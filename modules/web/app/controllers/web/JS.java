package controllers.web;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
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
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    Logger.debug(learnerSolution);
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

  public Result vorschau(int exerciseId) {
    return ok((new JsWebExercise()).vorschau());
  }

}
