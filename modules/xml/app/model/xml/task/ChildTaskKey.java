package model.xml.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ChildTaskKey implements Serializable {
  
  private static final long serialVersionUID = 7072289741394461703L;
  
  public int id;
  
  public int taskId;
  
  @Override
  public boolean equals(Object other) {
    if(other == null || !(other instanceof ChildTaskKey))
      return false;
    
    ChildTaskKey otherKey = (ChildTaskKey) other;
    return otherKey.id == id && otherKey.taskId == taskId;
  }
  
  @Override
  public int hashCode() {
    return 1000 * taskId + id;
  }
}