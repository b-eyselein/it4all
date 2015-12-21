package model.javascript;

import java.util.List;
import java.util.LinkedList;

public class JavascriptTest<ValueType, ResultType> {
  
  private List<ValueType> values = new LinkedList<ValueType>();
  private ResultType awaitedResult, gottenResult;
  
  public JavascriptTest(List<ValueType> testValues, ResultType awaitedTestResult) {
    values = testValues;
    awaitedResult = awaitedTestResult;
  }
  
  public ResultType getAwaitedResult() {
    return awaitedResult;
  }
  
  public ResultType getRealResult() {
    return gottenResult;
  }
  
  public List<ValueType> getTestValues() {
    return values;
  }
  
}
