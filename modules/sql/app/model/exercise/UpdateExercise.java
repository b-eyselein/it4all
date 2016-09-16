package model.exercise;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.UpdateCorrector;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

@Entity
@DiscriminatorValue("UPDATE")
public class UpdateExercise extends SqlExercise {
  
  private static final QueryCorrector<Update, Update> corrector = new UpdateCorrector();

  public UpdateExercise(SqlExerciseKey theKey) {
    super(theKey);
  }
  
  @Override
  public QueryCorrector<? extends Statement, ?> getCorrector() {
    return corrector;
  }
  
  @Override
  public String getType() {
    return "UPDATE";
  }
  
}
