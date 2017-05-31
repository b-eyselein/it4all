package model.result;

import model.exercise.Success;
import model.task.Condition;

public class ConditionResult extends EvaluationResult {

  public Condition condition;
  public boolean isPre;
  public String gottenValue;

  public ConditionResult(Success theSuccess, Condition theCondition, String theGottenValue, boolean isPrecondition) {
    super(theSuccess);
    condition = theCondition;
    gottenValue = theGottenValue;
    isPre = isPrecondition;
  }

}
