package model.javascript;

import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class JsTestResult {
  
  private JsTest test;
  private String realResult = "";
  
  public JsTestResult(JsTest test) {
    this.test = test;
  }
  
  private String buildEvaluateString() {
    for(JsTestvalue value: test.values)
      System.out.print("ex: " + test.exercise.id + ", test: " + test.id + "=?=" + value.test.id + ", value: "
          + value.id + " == " + value.value + "\t");
    System.out.println();
    
    List<String> valueList = test.values.stream().map(value -> value.value).collect(Collectors.toList());
    return test.exercise.functionName + "(" + String.join(", ", valueList) + ");";
  }
  
  public void eval(ScriptEngine engine) throws ScriptException {
    String toEvaluate = buildEvaluateString();
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
    return buildEvaluateString();
  }
  
  public boolean wasSuccessful() {
    return realResult.equals(test.awaitedResult);
  }
}
