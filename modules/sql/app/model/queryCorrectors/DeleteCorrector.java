package model.queryCorrectors;

import java.sql.Connection;

import model.correctionResult.SqlCorrectionResult;
import model.exercise.SqlExercise.SqlExType;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteCorrector extends QueryCorrector<Delete> {
  
  public DeleteCorrector() {
    super(SqlExType.DELETE);
  }
  
  @Override
  protected SqlCorrectionResult compareStatically(Delete parsedUserStatement, Delete parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected SqlCorrectionResult executeQuery(Delete parsedUserStatement, Delete parsedSampleStatement,
      Connection connection, String slaveDB, String scenarioName) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
