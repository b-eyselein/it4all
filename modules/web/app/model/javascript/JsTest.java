package model.javascript;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Success;
import play.Logger;
import play.data.validation.Constraints.Required;

@Entity
public class JsTest extends Model {

  @Id
  public int id;
  
  @Required
  public String awaitedResult;
  
  @OneToMany(mappedBy = "test")
  @JsonManagedReference
  public List<JsTestvalue> values;
  
  @ManyToOne
  @JsonBackReference
  public JsExercise exercise;
  
  public JsTestResult evaluate(ScriptEngine engine) {
    String toEvaluate = buildToEvaluate();
    Logger.debug(toEvaluate);
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
  
  private String buildToEvaluate() {
    List<String> valueList = values.stream().map(value -> value.value).collect(Collectors.toList());
    return exercise.functionName + "(" + String.join(", ", valueList) + ");";
  }
  
}
