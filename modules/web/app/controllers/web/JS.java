package controllers.web;

import java.util.List;
import java.util.Map;

import controllers.core.UserControl;
import controllers.core.Util;
import model.javascript.JsCorrector;
import model.javascript.JsExercise;
import model.javascript.JsTestResult;
import model.user.Secured;
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
  
  private static String serverUrl = Util.getServerUrl();

  public Result commit(int exerciseId) {
    Map<String, String[]> body = request().body().asFormUrlEncoded();
    String learnerSolution = body.get("editorContent")[0];

    List<JsTestResult> testResults = JsCorrector.correct(JsExercise.finder.byId(exerciseId), learnerSolution);

    if(request().accepts("application/json"))
      return ok(Json.toJson(testResults));
    else
      // TODO: jscorrect --> Nur für Endkorrektur ?!?
      return ok(jscorrect.render(learnerSolution, testResults, UserControl.getUser()));
  }

  public Result exercise(int id) {
    JsExercise exercise = JsExercise.finder.byId(id);

    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.JS.index() + "\">Startseite</a>.</p>"));
    
    return ok(js.render(UserControl.getUser(), exercise, serverUrl));

  }

  public Result index() {
    return ok(jsoverview.render(JsExercise.finder.all(), UserControl.getUser()));
  }

}
