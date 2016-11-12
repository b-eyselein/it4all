package model.exercise;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.querycorrectors.CreateCorrector;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.SelectCorrector;
import model.querycorrectors.update.DeleteCorrector;
import model.querycorrectors.update.InsertCorrector;
import model.querycorrectors.update.UpdateCorrector;
import net.sf.jsqlparser.statement.Statement;

@Entity
public class SqlExercise extends Model implements Exercise {

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

  @Override
  public String getExerciseIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public List<String> getSampleSolutions() {
    return Arrays.asList(samples.split("#"));
  }
  
  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String getTitle() {
    // TODO Auto-generated method stub
    return null;
  }

}
