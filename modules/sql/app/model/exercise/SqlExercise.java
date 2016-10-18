package model.exercise;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.queryCorrectors.CreateCorrector;
import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.SelectCorrector;
import model.queryCorrectors.update.DeleteCorrector;
import model.queryCorrectors.update.InsertCorrector;
import model.queryCorrectors.update.UpdateCorrector;
import net.sf.jsqlparser.statement.Statement;

@Entity
public class SqlExercise extends Model {
  
  public static final String SAMPLE_JOIN_CHAR = "#";
  
  public static final Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);
  
  @EmbeddedId
  public SqlExerciseKey key;
  
  @Column(columnDefinition = "text")
  public String text;
  
  @Column(columnDefinition = "text")
  public String samples;
  
  @ManyToOne
  @JoinColumn(name = "scenario_name", insertable = false, updatable = false)
  public SqlScenario scenario;
  
  public String validation;
  
  public SqlExercise(SqlExerciseKey theKey) {
    key = theKey;
  }
  
  public QueryCorrector<? extends Statement, ?> getCorrector() {
    switch(key.exercisetype) {
    case CREATE:
      return new CreateCorrector();
    case DELETE:
      return new DeleteCorrector();
    case INSERT:
      return new InsertCorrector();
    case SELECT:
      return new SelectCorrector();
    case UPDATE:
      return new UpdateCorrector();
    default:
      return null;
    }
  }
  
  public List<String> getSampleSolutions() {
    return Arrays.asList(samples.split("#"));
  }
  
}
