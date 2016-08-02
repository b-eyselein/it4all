package model.exercise;

import javax.persistence.Entity;

import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.SelectCorrector;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

@Entity
public class SelectExercise extends SqlExercise {

  private static final QueryCorrector<Select, PlainSelect> corrector = new SelectCorrector();

  public SelectExercise(SqlExerciseKey theKey) {
    super(theKey);
  }

  @Override
  public QueryCorrector<? extends Statement, ?> getCorrector() {
    return corrector;
  }

}
