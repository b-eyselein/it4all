package controllers.exercises;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import model.javascript.JsExercise;
import model.javascript.JsTest;
import model.user.Student;
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
    
    JsExercise ex = null;
    // if(exerciseId == 1)
    // ex = new JavascriptExercise.IntegerStandardTest();
    // else
    // ex = new JavascriptExercise.StringStandardTest();
    
    List<JsTest> testResults = correct(ex, learnerSolution);
    
    return ok(jscorrect.render(learnerSolution, testResults, Student.find.byId(session(Application.SESSION_ID_FIELD))));
  }
  
  private List<JsTest> correct(JsExercise exercise, String learnerSolution) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    
    try {
      // Lese programmierte Lernerlösung ein
      engine.eval(learnerSolution);
      
      // Evaluiere Lernerlösung mit Testwerten
      // for(JsTest test: exercise.getTests()) {
      // // TODO: testing!
      // List<String> values = test.getTestValues().stream().map(value -> {
      // return value.toString();
      // }).collect(Collectors.toList());
      // String toEvaluate = exercise.getFunctionName() + "(" +
      // String.join(", ", values) + ");";
      // String ergebnis = engine.eval(toEvaluate).toString();
      // test.setRealResult(ergebnis);
      // test.setSuccessful(ergebnis.equals(test.getAwaitedResult()));
      // }
      // return exercise.getTests();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }
  
  public Result exercise(int id) {
    if(JsExercise.finder.byId(id) != null)
      return ok(js.render(Student.find.byId(session(Application.SESSION_ID_FIELD)), JsExercise.finder.byId(id)));
    else
      return badRequest("Diese Aufgabe existert leider nicht...");
  }
  
  public Result index() {
    return ok(jsoverview.render(Student.find.byId(session(Application.SESSION_ID_FIELD)), JsExercise.finder.all()));
  }
}
