package model.exercise;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.queryCorrectors.InsertCorrector;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.insert.Insert;

@Entity
@DiscriminatorValue("INSERT")
public class InsertExercise extends SqlExercise {

  private static final QueryCorrector<Insert, Insert, InsertExercise> corrector = new InsertCorrector();

  public InsertExercise(SqlExerciseKey theKey) {
    super(theKey);
  }

  @Override
  public QueryCorrector<Insert, Insert, InsertExercise> getCorrector() {
    return corrector;
  }

}
