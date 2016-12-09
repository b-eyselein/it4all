package model.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class JsConditionKey implements Serializable {

  private static final long serialVersionUID = 1L;

  public int id; // NOSONAR

  public int taskId; // NOSONAR

  public int exerciseId; // NOSONAR

  public JsConditionKey(int theId, int theTaskId, int theExerciseId) {
    id = theId;
    taskId = theTaskId;
    exerciseId = theExerciseId;
  }

  @Override
  public boolean equals(Object other) {
    if(other == null || !(other instanceof JsConditionKey))
      return false;

    JsConditionKey otherKey = (JsConditionKey) other;
    return otherKey.id == id && otherKey.taskId == taskId && otherKey.exerciseId == exerciseId;
  }

  @Override
  public int hashCode() {
    return 10_000 * exerciseId + 100 * taskId + id;
  }

}
