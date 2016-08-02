package model.exercise;

import javax.persistence.Entity;

import model.queryCorrectors.DeleteCorrector;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

@Entity
public class UpdateExercise extends SqlExercise {

  private static final QueryCorrector<Delete, Delete> corrector = new DeleteCorrector();
  
  public UpdateExercise(SqlExerciseKey theKey) {
    super(theKey);
  }

  @Override
  public QueryCorrector<? extends Statement, ?> getCorrector() {
    return corrector;
  }

}