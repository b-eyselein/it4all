package model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlQueryResult {
  
  public static class SqlCell {
    
    private String content;
    private boolean different = false;
    
    public SqlCell(String theContent) {
      content = theContent;
    }
    
    @Override
    public boolean equals(Object object) {
      if(object == null || !(object instanceof SqlCell))
        return false;
      SqlCell other = (SqlCell) object;
      return (content == null && other.content == null) || content.equals(other.content);
    }
    
    public String getAsHtml() {
      return "<td" + (different ? " class=\"danger\"" : "") + ">" + content + "</td>";
    }
    
    public String getContent() {
      return content;
    }
    
    public void markAsDifferent() {
      different = true;
    }
    
  }
  
  public static class SqlRow {
    
    public Map<String, SqlCell> cells = new HashMap<>();
    
    public void addCell(String columnName, SqlCell cell) {
      cells.put(columnName, cell);
    }
    
    public String getAsHtml(List<String> columnNames) {
      return "<tr>" + columnNames.stream().map(colName -> cells.get(colName).getAsHtml()).collect(Collectors.joining())
          + "</tr>";
    }
    
    public SqlCell getSqlCell(String columnName) {
      return cells.get(columnName);
    }
    
    public int size() {
      return cells.size();
    }
    
  }
  
  private static boolean checkCell(SqlCell firstCell, SqlCell secondCell) {
    if((firstCell == null ^ secondCell == null) || !firstCell.equals(secondCell)) {
      firstCell.markAsDifferent();
      secondCell.markAsDifferent();
      return false;
    } else {
      return true;
    }
  }
  
  private static boolean checkCells(SqlQueryResult first, SqlQueryResult second, List<String> columnNames) {
    List<SqlRow> firstRows = first.getRows(), secondRows = second.getRows();
    if(firstRows.size() != secondRows.size())
      return false;
    
    int rowCount = firstRows.size();
    boolean identic = true;
    for(int rowCounter = 0; rowCounter < rowCount; rowCounter++) {
      SqlRow firstRow = firstRows.get(rowCounter), secondRow = secondRows.get(rowCounter);
      if(firstRow.size() != secondRow.size())
        return false;
      for(String colName: columnNames)
        identic &= checkCell(firstRow.getSqlCell(colName), secondRow.getSqlCell(colName));
    }
    
    return identic;
  }
  
  private static boolean checkColumnNames(SqlQueryResult first, SqlQueryResult second) {
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
  
  private List<SqlRow> rows;
  
  private String tableName = "";
  
  public SqlQueryResult(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();
    
    colNames = new ArrayList<>(columnCount);
    for(int i = 1; i <= columnCount; i++)
      colNames.add(metaData.getColumnName(i));
    
    rows = new LinkedList<>();
    while(resultSet.next()) {
      SqlRow row = new SqlRow();
      for(int i = 1; i <= columnCount; i++) {
        String columnName = colNames.get(i - 1);
        String content = resultSet.getString(columnName);
        row.addCell(columnName, new SqlCell(content));
      }
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
  
  public List<SqlRow> getRows() {
    return rows;
  }
  
  public String getTableName() {
    return tableName;
  }
  
  public boolean isIdentic(SqlQueryResult other) {
    return checkColumnNames(this, other) && checkCells(this, other, colNames);
  }
  
  public String toHtmlTable() {
    String body = rows.stream().map(row -> row.getAsHtml(colNames)).collect(Collectors.joining());
    
    String table = "<div class=\"table-responsive\">";
    table += "<table class=\"table table-bordered table-condensed\">";
    table += "<thead><tr><th>" + String.join("</th><th>", colNames) + "</th></tr></thead>";
    table += "<tbody>" + body + "</tbody></table></div>";
    
    return table;
  }
  
}