package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
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
    List<String> tablesInUserStatement = tableNameFinder.getTableList(parsedStatement);
    List<String> tablesInSampleStatement = tableNameFinder.getTableList(parsedSampleStatement);
    
    List<String> tablesNotUsed = new LinkedList<>(tablesInSampleStatement);
    tablesNotUsed.removeAll(tablesInUserStatement);
    List<String> tablesNotNeeded = new LinkedList<>(tablesInUserStatement);
    tablesNotNeeded.removeAll(tablesInSampleStatement);

    String message = "Die Anzahl an verwendeten Tabellen stimmt nicht überein!";
    if(tablesNotUsed.size() > 0)
      message += "\nDiese Tabellen fehlen: " + String.join(", ", tablesNotUsed);
    if(tablesNotNeeded.size() > 0)
      message += "\nDiese Tabelle werden nicht benötigt: " + String.join(", ", tablesNotNeeded);
    
    if(tablesInSampleStatement.size() != tablesInUserStatement.size())
      return new SqlCorrectionResult(Success.PARTIALLY, message);
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
        return new SqlCorrectionResult(Success.COMPLETE, "Passt...?");
      else
        return new SqlCorrectionResult(Success.PARTIALLY, "Resultate waren nicht identisch!");
      
    } catch (SQLException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab ein Problem beim Ausführen der Query: " + e.getMessage());
    }
  }
  
}
