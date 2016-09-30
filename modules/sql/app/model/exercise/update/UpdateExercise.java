package model.exercise.update;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.update.UpdateCorrector;
import net.sf.jsqlparser.statement.update.Update;

@Entity
@DiscriminatorValue("UPDATE")
public class UpdateExercise extends SqlExercise {

  private static final QueryCorrector<Update, Update> corrector = new UpdateCorrector();

  public String validation;

  public UpdateExercise(SqlExerciseKey theKey) {
    super(theKey, SqlExerciseType.UPDATE);
  }

  @Override
  public QueryCorrector<Update, Update> getCorrector() {
    return corrector;
  }

}
