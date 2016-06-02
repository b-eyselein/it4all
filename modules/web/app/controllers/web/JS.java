package controllers.web;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.javascript.JsCorrector;
import model.javascript.JsExercise;
import model.javascript.JsTestResult;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.javascript.js;
import views.html.javascript.jsoverview;
import views.html.javascript.jscorrect;

@Security.Authenticated(Secured.class)
public class JS extends Controller {
  
  @Inject
  Util util;

  public Result commit(int exerciseId) {
    Map<String, String[]> body = request().body().asFormUrlEncoded();
    String learnerSolution = body.get("editorContent")[0];

    List<JsTestResult> testResults = JsCorrector.correct(JsExercise.finder.byId(exerciseId), learnerSolution);

    if(request().accepts("application/json"))
      return ok(Json.toJson(testResults));
    else
      // TODO: jscorrect --> Nur für Endkorrektur ?!?
      return ok(jscorrect.render(learnerSolution, testResults, UserManagement.getCurrentUser()));
  }

  public Result exercise(int id) {
    JsExercise exercise = JsExercise.finder.byId(id);

    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.JS.index() + "\">Startseite</a>.</p>"));
    
    return ok(js.render(UserManagement.getCurrentUser(), exercise, util.getServerUrl()));

  }

  public Result index() {
    return ok(jsoverview.render(JsExercise.finder.all(), UserManagement.getCurrentUser()));
  }

}
