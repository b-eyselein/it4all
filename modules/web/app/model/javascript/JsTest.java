package model.javascript;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import model.exercise.Success;
import play.data.validation.Constraints.Required;

@Entity
public class JsTest extends Model {

  private static final String VALUES_SPLIT_CHAR = "#";

  @Id
  public int id;

  @Required
  public String awaitedResult;

  @Column(columnDefinition = "text")
  public String testvalues;

  @ManyToOne
  @JsonBackReference
  public JsExercise exercise;

  public JsTestResult evaluate(ScriptEngine engine) {
    String toEvaluate = buildToEvaluate();
    try {
      String realResult = engine.eval(toEvaluate).toString();
      if(realResult.equals(awaitedResult))
        return new JsTestResult(this, Success.COMPLETE, toEvaluate, realResult);
      else
        return new JsTestResult(this, Success.PARTIALLY, toEvaluate, realResult);
    } catch (ScriptException | NullPointerException e) {
      return new JsTestResult(this, Success.NONE, toEvaluate, "");
    }
  }

  public List<String> getTestValues() {
    return Arrays.asList(testvalues.split(VALUES_SPLIT_CHAR));
  }

  private String buildToEvaluate() {
    return exercise.functionName + "(" + String.join(", ", getTestValues()) + ");";
  }

}
