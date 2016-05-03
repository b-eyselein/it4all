package model.xml.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TaskKey implements Serializable {

  private static final long serialVersionUID = 2816287031436265005L;

  public int id;

  public int exerciseId;

  @Override
  public boolean equals(Object other) {
    if(other == null || !(other instanceof TaskKey))
      return false;

    TaskKey otherKey = (TaskKey) other;
    return otherKey.id == id && otherKey.exerciseId == exerciseId;
  }

  @Override
  public int hashCode() {
    return 1000 * exerciseId + id;
  }
}
