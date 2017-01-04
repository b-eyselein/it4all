package model;

import model.programming.ITestData;

public interface JsTestData extends ITestData<JsExercise> {
  
  @Override
  public default String buildToEvaluate() {
    return getExercise().functionname + "(" + String.join(", ", getInput()) + ");";
  }
  
}
