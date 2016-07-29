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

    public SqlSampleSolutionKey(int theSampleId, int theExerciseId, String theScenarioName) {
      sampleId = theSampleId;
      exerciseId = theExerciseId;
      scenarioName = theScenarioName;
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

  public static final Finder<SqlSampleSolutionKey, SqlSampleSolution> finder = new Finder<>(SqlSampleSolution.class);

  @EmbeddedId
  public SqlSampleSolutionKey key;

  public String sample;

  @ManyToOne
  // @formatter:off
  @JoinColumns({
      @JoinColumn(name = "scenario_name", referencedColumnName = "scenario_name", updatable = false, insertable = false),
      @JoinColumn(name = "exercise_id", referencedColumnName = "id", updatable = false, insertable = false)
  })
  //@formatter:on
  public SqlExercise exercise;

  public SqlSampleSolution(SqlSampleSolutionKey theKey) {
    key = theKey;
  }

}
