package model.exercise.update;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.update.InsertCorrector;
import net.sf.jsqlparser.statement.insert.Insert;

@Entity
@DiscriminatorValue("INSERT")
public class InsertExercise extends SqlExercise {
  
  private static final QueryCorrector<Insert, Insert, InsertExercise> corrector = new InsertCorrector();
  
  public String validation;

  public InsertExercise(SqlExerciseKey theKey) {
    super(theKey, SqlExerciseType.INSERT);
  }
  
  @Override
  public QueryCorrector<Insert, Insert, InsertExercise> getCorrector() {
    return corrector;
  }
  
}
