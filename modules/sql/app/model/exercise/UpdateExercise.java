package model.exercise;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.UpdateCorrector;
import net.sf.jsqlparser.statement.update.Update;

@Entity
@DiscriminatorValue("UPDATE")
public class UpdateExercise extends SqlExercise {

  private static final QueryCorrector<Update, Update, UpdateExercise> corrector = new UpdateCorrector();

  public String validation;

  public UpdateExercise(SqlExerciseKey theKey) {
    super(theKey);
  }

  @Override
  public QueryCorrector<Update, Update, UpdateExercise> getCorrector() {
    return corrector;
  }

}
