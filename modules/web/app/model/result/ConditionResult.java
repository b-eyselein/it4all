package model.result;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;
import model.task.Condition;

public class ConditionResult extends EvaluationResult {

  public Condition condition;
  public boolean isPre;
  public String gottenValue;

  public ConditionResult(Success theSuccess, Condition theCondition, String theGottenValue, boolean isPrecondition) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theSuccess);
    condition = theCondition;
    gottenValue = theGottenValue;
    isPre = isPrecondition;
  }

}
