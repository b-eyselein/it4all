package model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SqlQueryResult {
  
  private List<String> colNames;
  
  // TODO: evtl. eigene Klasse?
  private List<List<String>> rows;
  
  public SqlQueryResult(ResultSet resultSet) throws SQLException {
    this(resultSet, false);
  }
  
  public SqlQueryResult(ResultSet resultSet, boolean sort) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();
    
    colNames = new ArrayList<String>(columnCount);
    for(int i = 1; i <= columnCount; i++)
      colNames.add(metaData.getColumnName(i));
    
    if(sort)
      // Sort column names to ignore order
      Collections.sort(colNames);
    
    rows = new LinkedList<List<String>>();
    while(resultSet.next()) {
      List<String> row = new ArrayList<String>(columnCount);
      for(int i = 1; i <= columnCount; i++)
        row.add(resultSet.getString(colNames.get(i - 1)));
      rows.add(row);
    }
    
  }
  
  public int getColumnCount() {
    return colNames.size();
  }
  
  public List<String> getColumnNames() {
    return colNames;
  }
  
  public List<List<String>> getRows() {
    return rows;
  }
  
  public boolean isIdentic(SqlQueryResult other) {
    // FIXME: implement comparison!
    
    // Check columnNames with order
    int columnCount = colNames.size();
    if(other.getColumnCount() != columnCount)
      return false;
    for(int i = 0; i < columnCount; i++)
      if(!other.getColumnNames().get(i).equals(colNames.get(i)))
        return false;
    
    // Check rows
    int rowCount = rows.size();
    List<List<String>> otherRows = other.getRows();
    if(otherRows.size() != rowCount)
      return false;
    for(int rowCounter = 0; rowCounter < rowCount; rowCounter++) {
      List<String> otherRow = otherRows.get(rowCounter);
      List<String> thisRow = rows.get(rowCounter);
      if(otherRow.size() != thisRow.size())
        return false;
      for(int cellCounter = 0; cellCounter < thisRow.size(); cellCounter++)
        if(!otherRow.get(cellCounter).equals(thisRow.get(cellCounter)))
          return false;
    }
    
    return true;
  }
  
}