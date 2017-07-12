package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class WebSolutionKey implements Serializable {
  
  private static final long serialVersionUID = 1640011527742075003L;
  
  private String username;
  
  private int exerciseId;
  
  public WebSolutionKey(String theUserName, int theExerciseId) {
    username = theUserName;
    exerciseId = theExerciseId;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof WebSolutionKey && hashCode() == obj.hashCode();
  }

  public int getExerciseId() {
    return exerciseId;
  }
  
  public String getUsername() {
    return username;
  }
  
  @Override
  public int hashCode() {
    return IntConsts.THOUSAND * username.hashCode() + exerciseId;
  }
  
}
