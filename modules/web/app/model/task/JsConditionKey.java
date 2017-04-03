package model.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class JsConditionKey implements Serializable {
  
  private static final long serialVersionUID = -6543551468151L;
  
  public int exerciseId; // NOSONAR
  public int taskId; // NOSONAR
  public int id; // NOSONAR
  
  public JsConditionKey(int theId, int theTaskId, int theExerciseId) {
    exerciseId = theExerciseId;
    taskId = theTaskId;
    id = theId;
  }
  
  @Override
  public boolean equals(Object other) {
    if(!(other instanceof JsConditionKey))
      return false;
    
    JsConditionKey otherKey = (JsConditionKey) other;
    return otherKey.id == id && otherKey.taskId == taskId && otherKey.exerciseId == exerciseId;
  }
  
  @Override
  public int hashCode() {
    return 1_000_000 * exerciseId + 1_000 * taskId + id;
  }
  
}
