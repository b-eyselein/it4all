package model.exercise;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class GradingKey implements Serializable {
  
  private static final long serialVersionUID = -1474660743836662259L;
  
  public String userName;
  
  public int exerciseId;
  
  public GradingKey(String theUserName, int theExerciseId) {
    userName = theUserName;
    exerciseId = theExerciseId;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof GradingKey))
      return false;
    
    GradingKey otherKey = (GradingKey) obj;
    return otherKey.userName.equals(userName) && otherKey.exerciseId == exerciseId;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + exerciseId;
    result = prime * result + ((userName == null) ? 0 : userName.hashCode());
    return result;
  }
  
}
