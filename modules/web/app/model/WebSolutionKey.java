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
    return obj instanceof WebSolutionKey && hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return IntConsts.THOUSAND * userName.hashCode() + exerciseId;
  }

}
