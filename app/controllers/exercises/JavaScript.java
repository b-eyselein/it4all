package controllers.exercises;

import java.util.LinkedList;
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
import controllers.Application;
import controllers.Secured;
import views.html.javascript.*;

@Security.Authenticated(Secured.class)
public class JavaScript extends Controller {
  
  // private static String[][] testValues = {{"1", "1", "2"}, {"1", "2", "3"},
  // {"2", "2", "4"}, {"4", "7", "11"},
  // {"83", "74", "157"}};
  // private static String exerciseText =
  // "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt und deren Summe zurückgibt.";
  // private static final String exercise =
  // "function sum(a, b) {\n  return 0;\n}";
  
  public Result commit() {
    Map<String, String[]> body = request().body().asFormUrlEncoded();
    String learnerSolution = body.get("editorContent")[0];
    
    LinkedList<String> testResults = correct(learnerSolution);
    
    return ok(javascriptcorrect.render(learnerSolution, testResults,
        Student.find.byId(session(Application.SESSION_ID_FIELD))));
  }
  
  private LinkedList<String> correct(String learnerSolution) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    LinkedList<String> testResults = new LinkedList<String>();
    
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
        String testValues = String.join(", ", values);
        Long ergebnis = (Long) engine.eval("sum(" + testValues + ");");
        
        if(ergebnis.equals(test.getRealResult()))
          testResults.add("Test von " + test.toString() + " war erfolgreich!");
        else
          testResults.add("Ergebnis von " + test.toString() + " war " + ergebnis + ", erwartet wurde "
              + test.getRealResult());
      }
      
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return testResults;
  }
  
  public Result index() {
    return ok(javascript.render(Student.find.byId(session(Application.SESSION_ID_FIELD)), "", ""));
    // return
    // ok(javascript.render(Student.find.byId(session(Application.SESSION_ID_FIELD)),
    // exerciseText, exercise));
  }
}
