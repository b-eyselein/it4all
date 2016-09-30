package model.exercise;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.exercise.update.DeleteExercise;
import model.exercise.update.InsertExercise;
import model.exercise.update.UpdateExercise;
import model.queryCorrectors.CreateCorrector;
import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.SelectCorrector;
import model.queryCorrectors.update.DeleteCorrector;
import model.queryCorrectors.update.InsertCorrector;
import model.queryCorrectors.update.UpdateCorrector;
import net.sf.jsqlparser.statement.Statement;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "querytype", discriminatorType = DiscriminatorType.STRING)
public abstract class SqlExercise extends Model {

  public static final String SAMPLE_JOIN_CHAR = "#";

  public static Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);

  public static SqlExercise instantiate(SqlExerciseKey exerciseKey, SqlExerciseType exerciseType) {
    switch(exerciseType) {
    case SELECT:
      return new SelectExercise(exerciseKey);
    case DELETE:
      return new DeleteExercise(exerciseKey);
    case UPDATE:
      return new UpdateExercise(exerciseKey);
    case CREATE:
      return new CreateExercise(exerciseKey);
    case INSERT:
      return new InsertExercise(exerciseKey);
    default:
      return null;
    }
  }

  @EmbeddedId
  public SqlExerciseKey key;

  // Discriminatorcolumn...
  @Column(insertable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  public SqlExerciseType querytype;

  @Column(columnDefinition = "text")
  public String text;

  @Column(columnDefinition = "text")
  public String samples;

  @ManyToOne
  @JoinColumn(name = "scenario_name", insertable = false, updatable = false)
  public SqlScenario scenario;

  public String validation;

  public SqlExercise(SqlExerciseKey theKey, SqlExerciseType theType) {
    key = theKey;
    querytype = theType;
  }

  public QueryCorrector<? extends Statement, ?> getCorrector() {
    switch(querytype) {
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
