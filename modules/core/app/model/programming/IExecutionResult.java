package model.programming;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public abstract class IExecutionResult extends EvaluationResult {

  public IExecutionResult(FeedbackLevel theMinimalFL, Success theSuccess, String... theMessages) {
    super(theMinimalFL, theSuccess, theMessages);
  }
  
  public abstract String getOutput();

  public abstract Object getResult();

}
