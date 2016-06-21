package model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import play.Logger;

public class SqlQueryResult {
  
  private static final Logger.ALogger theLogger = Logger.of("sql");

  private List<String> colNames;
  private List<List<String>> rows;

  public SqlQueryResult(ResultSet resultSet) {
    try {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();

      colNames = new ArrayList<String>(columnCount);
      for(int i = 1; i <= columnCount; i++)
        colNames.add(metaData.getColumnName(i));
      
      rows = new LinkedList<List<String>>();
      while(resultSet.next()) {
        List<String> row = new ArrayList<String>(columnCount);
        for(int i = 1; i <= columnCount; i++)
          row.add(resultSet.getString(i));
        rows.add(row);
      }

    } catch (SQLException e) {
      theLogger.error("Fehler bei Verarbeitung von ResultSet " + resultSet, e);
    }
  }

  public List<String> getColumnNames() {
    return colNames;
  }

  public List<List<String>> getRows() {
    return rows;
  }

  public boolean isIdentic(SqlQueryResult other) {
    // FIXME: implement comparison!
    if(other.getColumnNames().size() != colNames.size())
      return false;
    return true;
  }

}