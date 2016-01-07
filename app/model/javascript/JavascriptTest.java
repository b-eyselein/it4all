package model.javascript;

import java.util.List;
import java.util.LinkedList;

public class JavascriptTest<ValueType, ResultType> {
  
  private List<ValueType> values = new LinkedList<ValueType>();
  private ResultType awaitedResult;
  private Object gottenResult;
  private boolean wasSuccessful;
  
  public JavascriptTest(List<ValueType> testValues, ResultType awaitedTestResult) {
    values = testValues;
    awaitedResult = awaitedTestResult;
  }
  
  public ResultType getAwaitedResult() {
    return awaitedResult;
  }
  
  public Object getRealResult() {
    return gottenResult;
  }
  
  public List<ValueType> getTestValues() {
    return values;
  }
  
  public void setRealResult(Object realResult) {
    gottenResult = realResult;
  }
  
  public void setSuccessful(boolean testWasSuccessful) {
    wasSuccessful = testWasSuccessful;
  }
  
  public boolean wasSuccessful() {
    return wasSuccessful;
  }
  
}
