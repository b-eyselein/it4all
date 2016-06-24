package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.SqlCorrectionResult;
import model.SqlQueryResult;
import model.TableComparisonResult;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.Success;
import net.sf.jsqlparser.statement.select.Select;

public class SelectCorrector extends QueryCorrector<Select> {
  
  public SelectCorrector() {
    super(SqlExType.SELECT);
  }
  
  @Override
  protected TableComparisonResult compareUsedTables(Select parsedStatement, Select parsedSampleStatement) {
    List<String> tablesInUserStatement = tableNameFinder.getTableList(parsedStatement);
    List<String> tablesInSampleStatement = tableNameFinder.getTableList(parsedSampleStatement);
    
    List<String> missingTables = new LinkedList<>(tablesInSampleStatement);
    missingTables.removeAll(tablesInUserStatement);
    List<String> unneccessaryTables = new LinkedList<>(tablesInUserStatement);
    unneccessaryTables.removeAll(tablesInSampleStatement);
    
    return new TableComparisonResult().withMissingTables(missingTables).withUnneccessaryTables(unneccessaryTables);
  }
  
  @Override
  protected SqlCorrectionResult executeQuery(Select userStatement, Select sampleStatement, Connection conn,
      String slaveDB) {
    try {
      initializeDB(conn, slaveDB);
      conn.setCatalog(slaveDB);
      
      ResultSet userResultSet = conn.createStatement().executeQuery(userStatement.toString());
      SqlQueryResult userResult = new SqlQueryResult(userResultSet, true);
      
      ResultSet sampleResultSet = conn.createStatement().executeQuery(sampleStatement.toString());
      SqlQueryResult sampleResult = new SqlQueryResult(sampleResultSet, true);
      
      deleteDB(conn, slaveDB);
      
      if(userResult.isIdentic(sampleResult))
        return new SqlCorrectionResult(Success.COMPLETE, "Passt...?");
      else
        return new SqlCorrectionResult(Success.NONE, "Resultate waren nicht identisch!");
      
    } catch (SQLException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab ein Problem beim Ausführen der Query: " + e.getMessage());
    }
  }
  
}
