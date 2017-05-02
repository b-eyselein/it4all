package model.programming;

import model.exercise.Success;

public class SyntaxError extends AExecutionResult {

  private String description;

  public SyntaxError(String theDescription, String theOutput) {
    super(Success.NONE, theOutput);
    description = theDescription;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public Object getResult() {
    // TODO Auto-generated method stub
    return null;
  }

}
