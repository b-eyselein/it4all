package model.execution;

public class ExecutionResult extends AExecutionResult {
  
  private String result;
  
  public ExecutionResult(String theEvaluated, String theRealResult, String theConsoleOutput) {
    super(theEvaluated, theConsoleOutput);
    result = theRealResult;
  }
  
  @Override
  public String getEvaluated() {
    return evaluated;
  }
  
  @Override
  public String getResult() {
    return result;
  }
  
}