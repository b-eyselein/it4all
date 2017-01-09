package model;

import java.util.List;

import model.programming.ITestData;

public class PythonTestData implements ITestData {

  private List<String> inputs;
  private String output;

  public PythonTestData(List<String> theInputs, String theOutput) {
    inputs = theInputs;
    output = theOutput;
  }

  @Override
  public String buildToEvaluate(String functionname) {
    // TODO Auto-generated method stub
    return functionname + "(" + String.join(", ", inputs) + ")";
  }

  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 1;
  }

  @Override
  public List<String> getInput() {
    // TODO Auto-generated method stub
    return inputs;
  }

  @Override
  public String getOutput() {
    // TODO Auto-generated method stub
    return output;
  }

}
