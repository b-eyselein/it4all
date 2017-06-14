package model.testdata;

import java.io.Serializable;

import javax.persistence.Embeddable;

import model.IntConsts;

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
    return obj instanceof CommitedTestDataKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return IntConsts.MILLION * userName.hashCode() + IntConsts.THOUSAND * exerciseId + testId;
  }
  
}
