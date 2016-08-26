package model.javascript;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import model.exercise.Success;

@Entity
public class JsTest extends Model {
  
  @Id
  public int id;
  
  @ManyToOne
  @JsonBackReference
  public JsExercise exercise;
  
  @Embedded
  public SavedTestData testData;
  
  public JsTestResult evaluate(ScriptEngine engine) {
    String toEvaluate = buildToEvaluate();
    String realResult = "";

    try {
      realResult = engine.eval(toEvaluate).toString();
    } catch (ScriptException | NullPointerException e) {
      return new JsTestResult(this, Success.NONE, toEvaluate, "");
    }

    boolean validated = JsCorrector.validateResult(exercise.returntype, realResult, testData.getOutput());
    if(validated)
      return new JsTestResult(this, Success.COMPLETE, toEvaluate, realResult);
    else
      return new JsTestResult(this, Success.PARTIALLY, toEvaluate, realResult);
  }
  
  private String buildToEvaluate() {
    return exercise.functionName + "(" + String.join(", ", testData.getInput()) + ");";
  }
  
}
