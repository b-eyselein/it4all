package model.exercise;

import java.io.Serializable;

import javax.persistence.Embeddable;

import model.IntConsts;

@Embeddable
public class SqlSampleKey implements Serializable {
  
  private static final long serialVersionUID = 967886498435L;
  
  private int exerciseId;
  
  private int sampleId;
  
  public SqlSampleKey(int theExerciseId, int theSampleId) {
    exerciseId = theExerciseId;
    sampleId = theSampleId;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof SqlSampleKey && hashCode() == obj.hashCode();
  }
  
  public int getExerciseId() {
    return exerciseId;
  }
  
  public int getSampleId() {
    return sampleId;
  }
  
  @Override
  public int hashCode() {
    return IntConsts.MILLION * exerciseId + sampleId;
    
  }
  
}
