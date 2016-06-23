package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.SqlCorrectionResult;
import model.SqlExercise;
import model.SqlExercise.SqlExType;
import model.SqlQueryResult;
import model.exercise.Success;
import net.sf.jsqlparser.statement.select.Select;

public class SelectCorrector extends QueryCorrector<Select> {

  public SelectCorrector() {
    super(SqlExType.SELECT);
  }
  
  @Override
  protected SqlCorrectionResult compareUsedTables(Select parsedStatement, Select parsedSampleStatement) {
    List<String> tablesInUserStatement = TABLE_NAME_FINDER.getTableList(parsedStatement);
    List<String> tablesInSampleStatement = TABLE_NAME_FINDER.getTableList(parsedSampleStatement);
    if(tablesInSampleStatement.size() != tablesInUserStatement.size())
      return new SqlCorrectionResult(Success.PARTIALLY, "Die Anzahl an verwendeten Tabellen stimmt nicht überein!");
    else
      return null;
  }
  
  @Override
  protected SqlCorrectionResult correctSpecialForQuery(Select parsedUserStatement, SqlExercise exercise,
      Connection connection) {
    try {
      ResultSet userResultSet = connection.createStatement().executeQuery(parsedUserStatement.toString());
      SqlQueryResult userResult = new SqlQueryResult(userResultSet, true);
      
      ResultSet sampleResultSet = connection.createStatement().executeQuery(exercise.sample);
      SqlQueryResult sampleResult = new SqlQueryResult(sampleResultSet, true);
      if(userResult.isIdentic(sampleResult))
        return new SqlCorrectionResult(Success.COMPLETE, "Passt...");
      else
        return new SqlCorrectionResult(Success.PARTIALLY, "Es gab einen Fehler...");
    } catch (SQLException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab ein Problem beim Ausführen der Query: " + e.getMessage());
    }
  }
  
}
