package model.testdata;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CommitedTestDataKey implements Serializable {

  private static final long serialVersionUID = -365435546184358L;

  public String userName;

  public int exerciseId;

  public int testId;

  public CommitedTestDataKey(String theUserName, int theExerciseId, int theTestId) {
    userName = theUserName;
    exerciseId = theExerciseId;
    testId = theTestId;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof CommitedTestDataKey))
      return false;
    CommitedTestDataKey other = (CommitedTestDataKey) obj;
    return userName.equals(other.userName) && exerciseId == other.exerciseId && testId == other.testId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + exerciseId;
    result = prime * result + testId;
    result = prime * result + ((userName == null) ? 0 : userName.hashCode());
    return result;
  }

}
