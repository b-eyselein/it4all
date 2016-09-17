package model.exercise;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.queryCorrectors.DeleteCorrector;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.delete.Delete;

@Entity
@DiscriminatorValue("DELETE")
public class DeleteExercise extends SqlExercise {
  
  private static final QueryCorrector<Delete, Delete, DeleteExercise> corrector = new DeleteCorrector();
  
  public String validation;

  public DeleteExercise(SqlExerciseKey theKey) {
    super(theKey);
  }
  
  @Override
  public QueryCorrector<Delete, Delete, DeleteExercise> getCorrector() {
    return corrector;
  }
  
}
