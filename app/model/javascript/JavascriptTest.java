package model.javascript;

import java.util.List;
import java.util.LinkedList;

public class JavascriptTest {
  
  private List<String> values = new LinkedList<String>();
  private String awaitedResult;
  private String gottenResult;
  private boolean wasSuccessful;
  
  public JavascriptTest(List<String> testValues, String awaitedTestResult) {
    values = testValues;
    awaitedResult = awaitedTestResult;
  }
  
  public String getAwaitedResult() {
    return awaitedResult;
  }
  
  public String getRealResult() {
    return gottenResult;
  }
  
  public List<String> getTestValues() {
    return values;
  }
  
  public void setRealResult(String realResult) {
    gottenResult = realResult;
  }
  
  public void setSuccessful(boolean testWasSuccessful) {
    wasSuccessful = testWasSuccessful;
  }
  
  public boolean wasSuccessful() {
    return wasSuccessful;
  }
  
}
