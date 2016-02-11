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
import views.html.javascript.jsoverview;
import controllers.Application;
import controllers.Secured;

@Security.Authenticated(Secured.class)
public class JavaScript extends Controller {
  
  public Result commit(int exerciseId) {
    Map<String, String[]> body = request().body().asFormUrlEncoded();
    String learnerSolution = body.get("editorContent")[0];
    
    System.out.println(learnerSolution);
    
    List<JsTestResult> testResults = correct(JsExercise.finder.byId(exerciseId), learnerSolution);
    
    List<String> results = testResults.stream().map(res -> res.getAsString()).collect(Collectors.toList());
    return ok(String.join("\n", results));
    
    // TODO: Wird jscorrecot.scala.html gebraucht? --> Nur für Endkorrektur ?!?
    // return ok(jscorrect.render(learnerSolution, testResults,
    // Application.getUser()));
  }
  
  private List<JsTestResult> correct(JsExercise exercise, String learnerSolution) {
    try {
      ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
      
      // Lese programmierte Lernerlösung ein
      engine.eval(learnerSolution);
      
      // Evaluiere Lernerlösung mit Testwerten
      List<JsTestResult> testResults = getTestResults(exercise.functionTests);
      for(JsTestResult testResult: testResults)
        testResult.eval(engine);
      return testResults;
    } catch (ScriptException e) {
      // TODO: Log Exception or submit to learner?
      e.printStackTrace();
      return Collections.emptyList();
    }
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
