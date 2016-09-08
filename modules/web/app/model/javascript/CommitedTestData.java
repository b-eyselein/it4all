package model.javascript;

import java.util.List;

public class CommitedTestData implements ITestData {
  
  private int id;
  private JsExercise exercise;
  private List<String> input;
  private String output;
  private boolean ok;
  
  public CommitedTestData(JsExercise theExercise, int theId, List<String> theInput) {
    id = theId;
    exercise = theExercise;
    input = theInput.subList(0, theInput.size() - 1);
    output = theInput.get(theInput.size() - 1);
  }
  
  @Override
  public JsExercise getExercise() {
    return exercise;
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
