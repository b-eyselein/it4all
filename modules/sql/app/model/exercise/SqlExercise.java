package model.exercise;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import model.queryCorrectors.CreateCorrector;
import model.queryCorrectors.DeleteCorrector;
import model.queryCorrectors.InsertCorrector;
import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.SelectCorrector;
import model.queryCorrectors.UpdateCorrector;
import net.sf.jsqlparser.statement.Statement;

@Entity
public class SqlExercise extends Model {

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

  public enum SqlExType {

    // @formatter:off
    CREATE(new CreateCorrector()),
    SELECT(new SelectCorrector()),
    UPDATE(new UpdateCorrector()),
    INSERT(new InsertCorrector()),
    DELETE(new DeleteCorrector());
    // @formatter:on
    
    public static SqlExType getByName(String typeText) {
      for(SqlExType type: values())
        if(type.toString().equals(typeText))
          return type;
      return null;
    }
    
    private QueryCorrector<? extends Statement> corrector;

    private SqlExType(QueryCorrector<? extends Statement> theCorrector) {
      corrector = theCorrector;
    }

    public QueryCorrector<? extends Statement> getCorrector() {
      return corrector;
    }

  }

  public static Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);

  @EmbeddedId
  public SqlExerciseKey key;

  @Column(columnDefinition = "text")
  public String text;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise")
  public List<SqlSampleSolution> samples;

  @Enumerated(EnumType.STRING)
  public SqlExType exType;

  @ManyToOne
  @JoinColumn(name = "scenario_name", insertable = false, updatable = false)
  public SqlScenario scenario;

  public SqlExercise(SqlExerciseKey theKey) {
    key = theKey;
  }

}
