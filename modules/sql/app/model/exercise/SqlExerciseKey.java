package model.exercise;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SqlExerciseKey implements Serializable {
  
  private static final long serialVersionUID = -38473516543541L;
  
  public int scenarioId;
  
  public int exerciseId;
  
  public SqlExerciseKey(int theScenarioId, int theExerciseId) {
    scenarioId = theScenarioId;
    exerciseId = theExerciseId;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof SqlExerciseKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return 1_000 * scenarioId + exerciseId;
  }
  
}
