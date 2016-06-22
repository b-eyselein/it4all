package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.SqlExercise;
import model.SqlQueryResult;
import model.SqlExercise.SqlExType;
import net.sf.jsqlparser.statement.select.Select;
import play.Logger;

public class SelectCorrector extends QueryCorrector<Select> {
  
  public SelectCorrector() {
    super(SqlExType.SELECT);
  }

  @Override
  protected void compareUsedTables(Select parsedStatement, Select parsedSampleStatement) {
    List<String> tablesInUserStatement = TABLE_NAME_FINDER.getTableList(parsedStatement);
    List<String> tablesInSampleStatement = TABLE_NAME_FINDER.getTableList(parsedSampleStatement);
    if(tablesInSampleStatement.size() != tablesInUserStatement.size())
      Logger.debug("Anzahl Tabellen ungleich!");
  }

  @Override
  protected String correctSpecialForQuery(Select parsedUserStatement, SqlExercise exercise, Connection connection) {
    try {
      ResultSet userResultSet = connection.createStatement().executeQuery(parsedUserStatement.toString());
      SqlQueryResult userResult = new SqlQueryResult(userResultSet, true);

      ResultSet sampleResultSet = connection.createStatement().executeQuery(exercise.sample);
      SqlQueryResult sampleResult = new SqlQueryResult(sampleResultSet, true);
      if(userResult.isIdentic(sampleResult))
        return "Passt...";
      else
        return "Fehler...";
    } catch (SQLException e) {
      return "Es gab ein Problem beim Ausführen der Query: " + e.getMessage();
    }
  }

}
