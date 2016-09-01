package model.javascript;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import model.exercise.Success;

public class CommitedTestData implements ITestData {

  private int id;
  private JsExercise exercise;
  private List<String> input;
  private String output;
  private boolean ok;

  public CommitedTestData(JsExercise theExercise, int theId, List<String> theInput, String theOutput) {
    id = theId;
    exercise = theExercise;
    input = theInput;
    output = theOutput;
  }

  @Override
  public JsTestResult evaluate(ScriptEngine engine) {
    String toEvaluate = buildToEvaluate();
    String realResult = "";

    try {
      realResult = engine.eval(toEvaluate).toString();
    } catch (ScriptException | NullPointerException e) {
      return new JsTestResult(this, Success.NONE, toEvaluate, "");
    }

    boolean validated = JsCorrector.validateResult(exercise.returntype, realResult, output);
    if(validated)
      return new JsTestResult(this, Success.COMPLETE, toEvaluate, realResult);
    else
      return new JsTestResult(this, Success.PARTIALLY, toEvaluate, realResult);
  }

  @Override
  public JsExercise getExercise() {
    return exercise;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public List<String> getInput() {
    return input;
  }

  @Override
  public String getOutput() {
    return output;
  }

  public boolean isOk() {
    return ok;
  }

  public void setOk(boolean isOk) {
    ok = isOk;
  }

}
