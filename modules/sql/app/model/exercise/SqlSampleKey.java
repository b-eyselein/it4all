package model.exercise;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SqlSampleKey implements Serializable {
  
  private static final long serialVersionUID = 967886498435L;
  
  public int exerciseId;
  
  public int sampleId;
  
  public SqlSampleKey(int theExerciseId, int theSampleId) {
    exerciseId = theExerciseId;
    sampleId = theSampleId;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof SqlSampleKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return 1_000 * exerciseId + sampleId;
    
  }
  
}
