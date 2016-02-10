package controllers.exercises;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import model.javascript.JsExercise;
import model.javascript.JsTest;
import model.javascript.JsTestResult;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.javascript.js;
import views.html.javascript.jscorrect;
import views.html.javascript.jsoverview;
import controllers.Application;
import controllers.Secured;

@Security.Authenticated(Secured.class)
public class JavaScript extends Controller {
  
  public Result commit(int exerciseId) {
    Map<String, String[]> body = request().body().asFormUrlEncoded();
    String learnerSolution = body.get("editorContent")[0];
    
    List<JsTestResult> testResults = correct(JsExercise.finder.byId(exerciseId), learnerSolution);
    
    return ok(jscorrect.render(learnerSolution, testResults, Application.getUser()));
  }
  
  private List<JsTestResult> correct(JsExercise exercise, String learnerSolution) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    
    try {
      // Lese programmierte Lernerlösung ein
      engine.eval(learnerSolution);
      
      List<JsTestResult> testResults = getTestResults(exercise.functionTests);
      
      // Evaluiere Lernerlösung mit Testwerten
      for(JsTestResult testResult: testResults) {
        testResult.eval(engine);
      }
      return testResults;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }
  
  public Result exercise(int id) {
    if(JsExercise.finder.byId(id) != null)
      return ok(js.render(Application.getUser(), JsExercise.finder.byId(id)));
    else
      return badRequest("Diese Aufgabe existert leider nicht.");
  }
  
  private List<JsTestResult> getTestResults(List<JsTest> functionTests) {
    return functionTests.stream().map(test -> new JsTestResult(test)).collect(Collectors.toList());
  }
  
  public Result index() {
    return ok(jsoverview.render(Application.getUser(), JsExercise.finder.all()));
  }
}
