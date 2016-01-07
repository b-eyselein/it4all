package controllers.exercises;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import model.javascript.JavascriptExercise;
import model.javascript.JavascriptTest;
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
  
  private static String exerciseText = "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt und deren Summe zurückgibt.";
  private static final String exercise = "function sum(a, b) {\n  return 0;\n}";
  
  public Result commit(int exercise) {
    Map<String, String[]> body = request().body().asFormUrlEncoded();
    String learnerSolution = body.get("editorContent")[0];
    
    List<JavascriptTest<Integer, Long>> testResults = correct(learnerSolution);
    
    return ok(jscorrect.render(learnerSolution, testResults, Student.find.byId(session(Application.SESSION_ID_FIELD))));
  }
  
  private List<JavascriptTest<Integer, Long>> correct(String learnerSolution) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    
    try {
      // Lese programmierte Lernerlösung ein
      engine.eval(learnerSolution);
      
      // Evaluiere Lernerlösung mit Testwerten
      JavascriptExercise<Integer, Long> exercise = new JavascriptExercise.IntegerStandardTest();
      for(JavascriptTest<Integer, Long> test: exercise.getTests()) {
        // TODO: testing!
        List<String> values = test.getTestValues().stream().map(value -> {
          return value.toString();
        }).collect(Collectors.toList());
        String toEvaluate = "sum(" + String.join(", ", values) + ");";
        Object ergebnis = engine.eval(toEvaluate);
        test.setRealResult(ergebnis);
        test.setSuccessful(ergebnis.equals(test.getAwaitedResult()));
      }
      return exercise.getTests();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Result exercise(int id) {
    JavascriptExercise ex = new JavascriptExercise.IntegerStandardTest();
    return ok(js.render(Student.find.byId(session(Application.SESSION_ID_FIELD)), ex));
  }
  
  public Result index() {
    return ok(jsoverview.render(Student.find.byId(session(Application.SESSION_ID_FIELD))));
  }
}
