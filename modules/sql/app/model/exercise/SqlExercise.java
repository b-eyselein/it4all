package model.exercise;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "querytype")
public abstract class SqlExercise extends Model {

  public static final String SAMPLE_JOIN_CHAR = "#";
  
  public static Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);
  
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
  
  public abstract QueryCorrector<? extends Statement, ?, ? extends SqlExercise> getCorrector();

  public List<String> getSampleSolution() {
    return Arrays.asList(samples.split("#"));
  }

  public abstract String getType();

}
