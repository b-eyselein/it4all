package model.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TaskKey implements Serializable {
  
  private static final long serialVersionUID = 2816287031436265005L;
  
  public int taskId; // NOSONAR
  
  public int exerciseId; // NOSONAR
  
  public TaskKey(int theTaskId, int theExerciseId) {
    taskId = theTaskId;
    exerciseId = theExerciseId;
  }
  
  @Override
  public boolean equals(Object other) {
    if(other == null || !(other instanceof TaskKey))
      return false;
    
    TaskKey otherKey = (TaskKey) other;
    return otherKey.taskId == taskId && otherKey.exerciseId == exerciseId;
  }
  
  @Override
  public int hashCode() {
    return 1000 * exerciseId + taskId;
  }
  
}