package model.exercise;

import javax.persistence.Entity;

import model.queryCorrectors.CreateCorrector;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

@Entity
public class CreateExercise extends SqlExercise {
  
  private static final QueryCorrector<CreateTable, CreateTable> corrector = new CreateCorrector();

  public CreateExercise(SqlExerciseKey theKey) {
    super(theKey);
  }

  @Override
  public QueryCorrector<? extends Statement, ?> getCorrector() {
    return corrector;
  }

}
