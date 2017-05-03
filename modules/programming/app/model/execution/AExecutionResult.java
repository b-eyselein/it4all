package model.execution;

public abstract class AExecutionResult {

  protected String evaluated;
  protected String consoleOutput;

  public AExecutionResult(String theEvaluated, String theConsoleOutput) {
    evaluated = theEvaluated;
    consoleOutput = theConsoleOutput;
  }

  public String getConsoleOutput() {
    return consoleOutput;
  }

  public String getEvaluated() {
    return evaluated;
  }

  public abstract String getResult();

}