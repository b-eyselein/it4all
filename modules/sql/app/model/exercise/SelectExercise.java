package model.exercise;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.SelectCorrector;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

@Entity
@DiscriminatorValue("SELECT")
public class SelectExercise extends SqlExercise {

  private static final QueryCorrector<Select, PlainSelect, SelectExercise> corrector = new SelectCorrector();

  public SelectExercise(SqlExerciseKey theKey) {
    super(theKey);
  }

  @Override
  public QueryCorrector<Select, PlainSelect, SelectExercise> getCorrector() {
    return corrector;
  }

}
