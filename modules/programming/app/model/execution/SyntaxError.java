package model.execution;

public class SyntaxError extends AExecutionResult {
  
  private String error;
  
  public SyntaxError(String theEvaluated, String theError, String theConsoleOutput) {
    super(theEvaluated, theConsoleOutput);
    error = theError;
  }
  
  public String getErrorMessage() {
    return error;
  }
  
  @Override
  public String getResult() {
    return error;
  }
  
}
