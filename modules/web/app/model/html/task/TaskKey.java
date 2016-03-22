package model.html.task;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TaskKey implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  public int id;
  
  public int exerciseid;
  
  @Override
  public boolean equals(final Object obj) {
    // FIXME!
    return super.equals(obj);
  }
  
  @Override
  public int hashCode() {
    // FIXME!
    return super.hashCode();
  }
}
