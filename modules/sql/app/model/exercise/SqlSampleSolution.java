package model.exercise;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class SqlSampleSolution extends Model {
  
  @Embeddable
  public static class SqlSampleSolutionKey implements Serializable {
    
    private static final long serialVersionUID = 51695198434897814L;
    
    public int sampleId;
    public int exerciseId;
    public String scenarioName;
    
    public SqlSampleSolutionKey(String theScenarioName, int theExerciseId, int theSampleId) {
      exerciseId = theExerciseId;
      scenarioName = theScenarioName;
      sampleId = theSampleId;
    }
    
    @Override
    public boolean equals(Object obj) {
      if(obj == null || !(obj instanceof SqlSampleSolutionKey))
        return false;
      SqlSampleSolutionKey other = (SqlSampleSolutionKey) obj;
      // @formatter:off
      return (other.sampleId == sampleId)
          && (other.exerciseId == exerciseId)
          && (other.scenarioName.equals(scenarioName));
      // @formatter:on
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + sampleId;
      result = prime * result + exerciseId;
      result = prime * result + ((scenarioName == null) ? 0 : scenarioName.hashCode());
      return result;
    }
  }
  
  @EmbeddedId
  public SqlSampleSolutionKey key;
  
  public String sample;
  
  @ManyToOne
  // @formatter:off
  @JoinColumns({
      @JoinColumn(name = "scenario_name", referencedColumnName = "scenario_name"),
      @JoinColumn(name = "exercise_id", referencedColumnName = "id")
  })
  //@formatter:on
  public SqlExercise exercise;

}
