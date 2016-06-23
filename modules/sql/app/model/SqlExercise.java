package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class SqlExercise extends Model {

  @Embeddable
  public static class SqlExerciseKey implements Serializable {

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

  public enum SqlExType {
    SELECT, UPDATE, INSERT, DELETE, CREATE;
  }

  public static Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);

  @EmbeddedId
  public SqlExerciseKey key;

  public String title;

  @Column(columnDefinition = "text")
  public String text;

  public String sample;

  @Enumerated(EnumType.STRING)
  public SqlExType exType;

  @ManyToOne
  @JoinColumn(name = "scenario_name", insertable = false, updatable = false)
  public SqlScenario scenario;

}
