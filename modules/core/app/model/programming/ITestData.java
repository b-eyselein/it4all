package model.programming;

import java.util.List;

public interface ITestData {

  public default String buildToEvaluate(String functionname) {
    return functionname + "(" + String.join(", ", getInput()) + ");";
  }

  public int getId();

  public List<String> getInput();

  public String getOutput();
}
