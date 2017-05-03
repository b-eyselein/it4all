package model.execution;

public class SyntaxError extends AExecutionResult {

  private String error = "TODO: ERROR!";

  public SyntaxError(String theEvaluated, String theConsoleOutput) {
    super(theEvaluated, theConsoleOutput);
  }

  @Override
  public String getResult() {
    return error;
  }

}
