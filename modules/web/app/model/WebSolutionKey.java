package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class WebSolutionKey implements Serializable {

  private static final long serialVersionUID = 1640011527742075003L;

  public String userName;

  public int exerciseId;

  public WebSolutionKey(String theUserName, int theExerciseId) {
    userName = theUserName;
    exerciseId = theExerciseId;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof WebSolutionKey))
      return false;
    WebSolutionKey other = (WebSolutionKey) obj;
    return exerciseId == other.exerciseId && userName.equals(other.userName);
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
