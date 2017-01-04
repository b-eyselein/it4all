package model.programming;

public class SyntaxError implements IExecutionResult {
  
  private String description;

  public SyntaxError(String theDescription) {
    description = theDescription;
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
