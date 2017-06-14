package model.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

import model.IntConsts;

@Embeddable
public class JsConditionKey implements Serializable {
  
  private static final long serialVersionUID = -6543551468151L;
  
  public int exerciseId;
  
  public int taskId;
  
  public int conditionId;
  
  public JsConditionKey(int theId, int theTaskId, int theExerciseId) {
    exerciseId = theExerciseId;
    taskId = theTaskId;
    conditionId = theId;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof JsConditionKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return IntConsts.MILLION * exerciseId + IntConsts.THOUSAND * taskId + conditionId;
  }
  
}
