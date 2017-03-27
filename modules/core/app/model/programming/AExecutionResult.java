package model.programming;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public abstract class AExecutionResult extends EvaluationResult {
  
  protected String output;
  
  public AExecutionResult(Success theSuccess, String theOutput, String... theMessages) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess, theMessages);
    output = theOutput;
  }
  
  public String getOutput() {
    return output;
  }
  
  public abstract Object getResult();
  
}
