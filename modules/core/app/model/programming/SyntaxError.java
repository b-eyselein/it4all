package model.programming;

import model.exercise.Success;

public class SyntaxError extends IExecutionResult {

  private String description;
  
  public SyntaxError(String theDescription) {
    super(null, Success.NONE);
    description = theDescription;
  }

  @Override
  public String getAsHtml() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String getOutput() {
    return description;
  }

  @Override
  public Object getResult() {
    // TODO Auto-generated method stub
    return null;
  }

}
