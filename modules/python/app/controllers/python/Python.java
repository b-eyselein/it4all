package controllers.python;

import java.io.StringWriter;

import javax.inject.Inject;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import controllers.core.UserManagement;
import model.exercise.EvaluationFailed;
import model.exercise.GenericEvaluationResult;
import model.exercise.Success;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.python;

public class Python extends Controller {
  
  @Inject
  private FormFactory factory;
  
  public Result commit() {
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    
    if(learnerSolution == null || learnerSolution.isEmpty())
      return ok(Json.toJson(new EvaluationFailed("Sie haben einen leeren String abgegeben!")));
    
    ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("python");
    ScriptContext context = new SimpleScriptContext();
    context.setWriter(new StringWriter());
    
    Object result = "";
    try {
      result = engine.eval(learnerSolution, context);
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    
    return ok(
        Json.toJson(new GenericEvaluationResult(Success.NONE, "The result was: " + (result != null ? result : "null"),
            "The output was:\n" + context.getWriter().toString())));
  }
  
  public Result index() {
    return ok(python.render(UserManagement.getCurrentUser()));
  }
  
}
