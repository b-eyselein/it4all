package model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlQueryResult {
  
  private static boolean checkCells(SqlQueryResult first, SqlQueryResult second) {
    List<List<String>> firstRows = first.getRows(), secondRows = second.getRows();
    if(firstRows.size() != secondRows.size())
      return false;
    
    int rowCount = firstRows.size();
    for(int rowCounter = 0; rowCounter < rowCount; rowCounter++) {
      List<String> firstRow = firstRows.get(rowCounter), secondRow = secondRows.get(rowCounter);
      if(firstRow.size() != secondRow.size())
        return false;
      int cellCount = firstRow.size();
      for(int cellCounter = 0; cellCounter < cellCount; cellCounter++) {
        String firstCell = firstRow.get(cellCounter), secondCell = secondRow.get(cellCounter);
        if(firstCell == null && secondCell == null)
          continue;
        if(firstCell == null || secondCell == null || !firstCell.equals(secondCell))
          return false;
      }
    }
    
    return true;
  }
  
  private static boolean checkColumns(SqlQueryResult first, SqlQueryResult second) {
    if(first.getColumnCount() != second.getColumnCount())
      return false;
    int columnCount = first.getColumnCount();
    List<String> firstColNames = first.getColumnNames(), secondColNames = second.getColumnNames();
    for(int i = 0; i < columnCount; i++)
      if(!firstColNames.get(i).toLowerCase().equals(secondColNames.get(i).toLowerCase()))
        return false;
    return true;
  }
  
  private List<String> colNames;
  
  // TODO: evtl. eigene Klasse?
  private List<List<String>> rows;
  
  private String tableName = "";
  
  public SqlQueryResult(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();
    
    colNames = new ArrayList<>(columnCount);
    for(int i = 1; i <= columnCount; i++)
      colNames.add(metaData.getColumnName(i));
    
    rows = new LinkedList<>();
    while(resultSet.next()) {
      List<String> row = new ArrayList<>(columnCount);
      for(int i = 1; i <= columnCount; i++)
        row.add(resultSet.getString(colNames.get(i - 1)));
      rows.add(row);
    }
    
  }
  
  public SqlQueryResult(ResultSet resultSet, String theTableName) throws SQLException {
    this(resultSet);
    tableName = theTableName;
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
  
  public String getTableName() {
    return tableName;
  }
  
  public boolean isIdentic(SqlQueryResult other) {
    return checkColumns(this, other) && checkCells(this, other);
  }
  
  public String toHtmlTable() {
    String body = rows.stream().map(cells -> {
      return cells.stream().collect(Collectors.joining("</td><td>", "<td>", "</td>"));
    }).collect(Collectors.joining("</tr></td>", "<tr>", "</tr>"));
    
    String table = "<div class=\"table-responsive\">";
    table += "<table class=\"table table-bordered table-condensed\">";
    table += "<thead><tr><th>" + String.join("</th><th>", colNames) + "</th></tr></thead>";
    table += "<tbody>" + body + "</tbody></table></div>";
    return table;
  }
  
}