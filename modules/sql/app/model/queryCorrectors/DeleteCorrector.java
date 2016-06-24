package model.queryCorrectors;

import java.sql.Connection;

import model.SqlCorrectionResult;
import model.exercise.SqlExercise.SqlExType;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteCorrector extends QueryCorrector<Delete> {
  
  public DeleteCorrector() {
    super(SqlExType.DELETE);
  }
  
  @Override
  protected SqlCorrectionResult compareUsedTables(Delete parsedUserStatement, Delete parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected SqlCorrectionResult executeQuery(Delete parsedUserStatement, Delete parsedSampleStatement,
      Connection connection, String slaveDB) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
