package model.javascript;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import model.exercise.Success;

public interface ITestData {

  public default String asStr() {
    return getId() + ": " + String.join(", ", getInput()) + " -> " + getOutput();
  }

  public default String buildToEvaluate() {
    return getExercise().functionname + "(" + String.join(", ", getInput()) + ");";
  }

  public default JsTestResult evaluate(ScriptEngine engine) {
    String toEvaluate = buildToEvaluate();
    String realResult = "";

    try {
      realResult = engine.eval(toEvaluate).toString();
    } catch (ScriptException | NullPointerException e) {
      return new JsTestResult(getOutput(), Success.NONE, toEvaluate, "");
    }

    boolean validated = JsCorrector.validateResult(getExercise().returntype, realResult, getOutput());
    Success success = validated ? Success.COMPLETE : Success.PARTIALLY;

    return new JsTestResult(getOutput(), success, toEvaluate, realResult);
  }

  public JsExercise getExercise();

  public int getId();

  public List<String> getInput();

  public String getOutput();
}
