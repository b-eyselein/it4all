package model.exercise;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class SqlExerciseKey implements Serializable, ExerciseIdentifier<SqlExerciseKey> {
  
  private static final long serialVersionUID = -670842276417613477L;
  
  public int id; // NOSONAR

  public String scenarioName; // NOSONAR
  
  @Enumerated(EnumType.STRING)
  public SqlExerciseType exercisetype;
  
  public SqlExerciseKey(String theScenarioName, int theExerciseId, SqlExerciseType theExerciseType) {
    id = theExerciseId;
    scenarioName = theScenarioName;
    exercisetype = theExerciseType;
  }
  
  @Override
  public SqlExerciseKey bind(String key, String txt) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof SqlExerciseKey))
      return false;
    SqlExerciseKey other = (SqlExerciseKey) obj;
    return (other.id == id) && (other.scenarioName.equals(scenarioName)) && (other.exercisetype == exercisetype);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((exercisetype == null) ? 0 : exercisetype.hashCode());
    result = prime * result + id;
    result = prime * result + ((scenarioName == null) ? 0 : scenarioName.hashCode());
    return result;
  }
  
  @Override
  public String javascriptUnbind() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String unbind(String key) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
