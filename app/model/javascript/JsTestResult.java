package model.javascript;

import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class JsTestResult {
  
  private JsTest test;
  private String toEvaluate;
  private String realResult = "";
  
  public JsTestResult(JsTest test) {
    this.test = test;
    List<String> valueList = test.values.stream().map(value -> value.value).collect(Collectors.toList());
    toEvaluate = test.exercise.functionName + "(" + String.join(", ", valueList) + ");";
    
  }
  
  public void eval(ScriptEngine engine) throws ScriptException {
    realResult = engine.eval(toEvaluate).toString();
  }
  
  public String getAwaitedResult() {
    return test.awaitedResult;
  }
  
  public String getRealResult() {
    return realResult;
  }
  
  public JsTest getTest() {
    return test;
  }
  
  public String getToEvaluate() {
    return toEvaluate;
  }
  
  public boolean wasSuccessful() {
    return realResult.equals(test.awaitedResult);
  }
}
