package model.programming;

import java.util.List;

public interface ITestData {
  
  public String buildToEvaluate(String functionname);

  public int getId();
  
  public List<String> getInput();
  
  public String getOutput();
}
