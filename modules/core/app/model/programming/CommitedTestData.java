package model.programming;

import java.util.List;

import model.programming.ITestData;

public class CommitedTestData implements ITestData {
  
  private int id;
  private List<String> input;
  private String output;
  private boolean ok;
  
  public CommitedTestData(int theId, List<String> theInput) {
    id = theId;
    input = theInput.subList(0, theInput.size() - 1);
    output = theInput.get(theInput.size() - 1);
  }
  
  public CommitedTestData(int theId, List<String> theInput, String theOutput) {
    id = theId;
    input = theInput;
    output = theOutput;
  }
  
  @Override
  public int getId() {
    return id;
  }
  
  @Override
  public List<String> getInput() {
    return input;
  }
  
  @Override
  public String getOutput() {
    return output;
  }
  
  public boolean isOk() {
    return ok;
  }
  
  public void setOk(boolean isOk) {
    ok = isOk;
  }
  
}
