package model.programming;

import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class ExecutionResult extends AExecutionResult {

  private String awaitedOutput;
  private String evaluated;
  private String realResult;

  public ExecutionResult(String theAwaitedOutput, Success theSuccess, String theEvaluated, String theRealResult,
      String theOutput) {
    super(theSuccess, theOutput);
    awaitedOutput = theAwaitedOutput;
    evaluated = theEvaluated;
    realResult = theRealResult;
    requestedFL = FeedbackLevel.FULL_FEEDBACK;
    output = theOutput;
  }

  public String getAwaitedResult() {
    return awaitedOutput;
  }

  public String getEvaluated() {
    return evaluated;
  }

  @Override
  public Object getResult() {
    return realResult;
  }

  public boolean wasSuccessful() {
    return success == Success.COMPLETE;
  }

}
