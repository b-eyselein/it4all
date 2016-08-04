package model.exercise;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.avaje.ebean.Model;

import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;

@MappedSuperclass
public abstract class SqlExercise extends Model {

  public static final String SAMPLE_JOIN_CHAR = "#";
  
  @EmbeddedId
  public SqlExerciseKey key;
  
  @Column(columnDefinition = "text")
  public String text;
  
  @Column(columnDefinition = "text")
  public String samples;
  
  @ManyToOne
  @JoinColumn(name = "scenario_name", insertable = false, updatable = false)
  public SqlScenario scenario;
  
  public SqlExercise(SqlExerciseKey theKey) {
    key = theKey;
  }

  public abstract QueryCorrector<? extends Statement, ?> getCorrector();

  public List<String> getSampleSolution() {
    return Arrays.asList(samples.split("#"));
  }

  public abstract String getType();

}
