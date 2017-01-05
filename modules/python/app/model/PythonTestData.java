package model;

import java.util.Collections;
import java.util.List;

import model.programming.ITestData;

public class PythonTestData implements ITestData {
  
  @Override
  public String buildToEvaluate(String functionname) {
    // TODO Auto-generated method stub
    return functionname + "()";
  }
  
  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 1;
  }
  
  @Override
  public List<String> getInput() {
    // TODO Auto-generated method stub
    return Collections.emptyList();
  }
  
  @Override
  public String getOutput() {
    // TODO Auto-generated method stub
    return "5";
  }

}
