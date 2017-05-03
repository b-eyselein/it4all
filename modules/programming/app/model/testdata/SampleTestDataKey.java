package model.testdata;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SampleTestDataKey implements Serializable {

  private static final long serialVersionUID = -7906767522614137149L;

  public int exerciseId;

  public int testId;

  public SampleTestDataKey(int theExerciseId, int theTestId) {
    exerciseId = theExerciseId;
    testId = theTestId;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof SampleTestDataKey && hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return 1_000 * exerciseId + testId;
  }

}