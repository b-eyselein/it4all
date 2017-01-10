package model.programming;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TestDataKey implements Serializable {

  private static final long serialVersionUID = -7906767522614137149L;

  public int exerciseId; // NOSONAR
  public int testId; // NOSONAR

  public TestDataKey(int theExerciseId, int theTestId) {
    exerciseId = theExerciseId;
    testId = theTestId;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof TestDataKey))
      return false;
    TestDataKey other = (TestDataKey) obj;
    return testId == other.testId && exerciseId == other.exerciseId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + exerciseId;
    result = prime * result + testId;
    return result;
  }

}