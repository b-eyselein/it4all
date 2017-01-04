package model.programming;

public class ExecutionResult implements IExecutionResult {

  private Object result;
  private String output;

  public ExecutionResult(Object theResult, String theOutput) {
    result = theResult;
    output = theOutput;
  }

  @Override
  public String getOutput() {
    return output;
  }

  @Override
  public Object getResult() {
    return result;
  }

}
