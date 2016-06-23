package model.queryCorrectors;

import java.sql.Connection;

import model.SqlCorrectionResult;
import model.SqlExercise;
import model.SqlExercise.SqlExType;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteCorrector extends QueryCorrector<Delete> {

  public DeleteCorrector() {
    super(SqlExType.DELETE);
  }

  @Override
  protected SqlCorrectionResult compareUsedTables(Delete parsedStatement, Delete parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected SqlCorrectionResult correctSpecialForQuery(Delete parsedStatement, SqlExercise exercise,
      Connection connection) {
    // TODO Auto-generated method stub
    return null;
  }

}
