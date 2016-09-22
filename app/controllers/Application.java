package controllers;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import controllers.core.UserManagement;
import model.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.index;

@Security.Authenticated(Secured.class)
public class Application extends Controller {
  
  public Result index() {
    return ok(index.render(UserManagement.getCurrentUser()));
  }

  public Result pythonTest() {

    ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("python");
    String result = "";

    try {
      engine.eval("def test(val):\n\treturn val + 5");
      result = engine.eval("test(5)").toString();
    } catch (ScriptException e) {
      e.printStackTrace();
    }

    return ok(new Html("The result was: " + result));
  }

}
