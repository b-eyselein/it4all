package model;

import model.programming.ITestData;

public interface JsTestData extends ITestData {

  @Override
  public default String buildToEvaluate(String functionname) {
    return functionname + "(" + String.join(", ", getInput()) + ");";
  }

}
