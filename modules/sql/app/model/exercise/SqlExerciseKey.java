package model.exercise;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SqlExerciseKey implements Serializable {

  private static final long serialVersionUID = -670842276417613477L;

  public int id;
  public String scenarioName;

  public SqlExerciseKey(String theScenarioName, int theExerciseId) {
    id = theExerciseId;
    scenarioName = theScenarioName;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof SqlExerciseKey))
      return false;
    SqlExerciseKey other = (SqlExerciseKey) obj;
    return (other.id == id) && (other.scenarioName.equals(scenarioName));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((scenarioName == null) ? 0 : scenarioName.hashCode());
    return result;
  }

}
