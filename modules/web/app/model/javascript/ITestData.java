package model.javascript;

import java.util.List;

import javax.script.ScriptEngine;

public interface ITestData {
  
  public default String asStr() {
    return getId() + ": " + String.join(", ", getInput()) + " -> " + getOutput();
  }

  public default String buildToEvaluate() {
    return getExercise().functionname + "(" + String.join(", ", getInput()) + ");";
  }
  
  public JsTestResult evaluate(ScriptEngine engine);
  
  public JsExercise getExercise();
  
  public int getId();
  
  public List<String> getInput();
  
  public String getOutput();
}
