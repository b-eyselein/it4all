package model.exercise;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;

@MappedSuperclass
public abstract class SqlExercise extends Model {

  @Embeddable
  public static class SqlExerciseKey implements Serializable {

    private static final long serialVersionUID = -670842276417613477L;

    public static final Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);

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

  @EmbeddedId
  public SqlExerciseKey key;

  @Column(columnDefinition = "text")
  public String text;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  public List<SqlSampleSolution> samples;

  @ManyToOne
  @JoinColumn(name = "scenario_name", insertable = false, updatable = false)
  public SqlScenario scenario;

  public SqlExercise(SqlExerciseKey theKey) {
    key = theKey;
  }

  public abstract QueryCorrector<? extends Statement, ?> getCorrector();
  
}
