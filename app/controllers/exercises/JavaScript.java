package controllers.exercises;

import java.util.LinkedList;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import model.user.Student;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import controllers.Application;
import controllers.Secured;
import views.html.javascript.*;

@Security.Authenticated(Secured.class)
public class JavaScript extends Controller {
  
  private static String[][] testValues = {{"1", "1", "2"}, {"1", "2", "3"}, {"4", "7", "11"}, {"83", "74", "157"}};
  private static final String exercise = "function sum(a, b) {\n  return 0;\n}";
  
  public Result commit() {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    
    LinkedList<String> testResults = new LinkedList<String>();
    
    Map<String, String[]> body = request().body().asFormUrlEncoded();
    String learnerSolution = body.get("editorContent")[0];
    
    try {
      engine.eval(learnerSolution);
      
      for(String[] row: testValues) {
        String summand1 = row[0], summand2 = row[1], summe = row[2];
        String addition = "sum(" + summand1 + ", " + summand2 + ");";
        String ergebnis;
        ergebnis = engine.eval(addition).toString();
        
        if(ergebnis.equals(summe))
          testResults.add("Test von " + addition + " war erfolgreich!");
        else
          testResults.add("Ergebnis von " + addition + " war " + engine.eval(addition) + ", erwartet wurde " + summe);
      }
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return ok(javascriptcorrect.render(learnerSolution, testResults,
        Student.find.byId(session(Application.SESSION_ID_FIELD))));
  }
  
  public Result index() {
    return ok(javascript.render(Student.find.byId(session(Application.SESSION_ID_FIELD)), exercise));
  }
}
