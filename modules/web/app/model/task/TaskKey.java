package model.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TaskKey implements Serializable {

  private static final long serialVersionUID = 2816287031436265005L;

  public int taskId;

  public int exerciseId;

  public TaskKey(int theTaskId, int theExerciseId) {
    taskId = theTaskId;
    exerciseId = theExerciseId;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TaskKey && hashCode() == other.hashCode();
  }

  @Override
  public int hashCode() {
    return 1000 * exerciseId + taskId;
  }

}