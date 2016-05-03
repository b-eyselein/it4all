package model.javascript;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import play.Logger;

public class JsCorrector {
  
  public static List<JsTestResult> correct(JsExercise exercise, String learnerSolution) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    try {
      // Lese programmierte Lernerlösung ein
      engine.eval(learnerSolution);
    } catch (ScriptException e) {
      // TODO: Log Exception or submit to learner?
      Logger.error(e.getMessage());
      return Collections.emptyList();
    }
    
    // Evaluiere Lernerlösung mit Testwerten
    return exercise.functionTests.stream().map(test -> test.evaluate(engine)).collect(Collectors.toList());
    
  }
  
}
