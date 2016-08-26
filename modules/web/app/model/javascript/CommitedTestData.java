package model.javascript;

import java.util.List;
import java.util.stream.Collectors;

public class CommitedTestData implements ITestData {

  private int id;
  private List<String> input;
  private String output;
  private boolean ok;

  public CommitedTestData(int theId, List<String> theInput, String theOutput) {
    id = theId;
    input = theInput;
    output = theOutput;
  }

  @Override
  public List<String> getDataTypes() {
    // TODO Auto-generated method stub
    return null;
  }

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
  
  @Override
  public String toString() {
    return id + ": " + String.join(", ", input.stream().map(i -> "\"" + i + "\"").collect(Collectors.toList()))
        + " -> \"" + output + "\"";
  }

}
