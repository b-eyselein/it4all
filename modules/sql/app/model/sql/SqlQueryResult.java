package model.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SqlQueryResult {

  private List<String> colNames;

  private List<SqlRow> rows;

  private String tableName = "";

  private static boolean checkCell(SqlCell firstCell, SqlCell secondCell) {
    if(firstCell == null || secondCell == null)
      return false;
    if((firstCell == null ^ secondCell == null) || !firstCell.equals(secondCell)) {
      firstCell.markAsDifferent();
      secondCell.markAsDifferent();
      return false;
    } else {
      return true;
    }
  }

  private static boolean checkCells(SqlQueryResult first, SqlQueryResult second, List<String> columnNames) {
    List<SqlRow> firstRows = first.getRows();
    List<SqlRow> secondRows = second.getRows();

    if(firstRows.size() != secondRows.size())
      return false;

    int rowCount = firstRows.size();
    boolean identic = true;
    
    for(int rowCounter = 0; rowCounter < rowCount; rowCounter++) {
      SqlRow firstRow = firstRows.get(rowCounter);
      SqlRow secondRow = secondRows.get(rowCounter);
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

    final List<String> firstColNames = first.getColumnNames();
    final List<String> secondColNames = second.getColumnNames();

    for(int i = 0; i < columnCount; i++)
      if(!firstColNames.get(i).equalsIgnoreCase(secondColNames.get(i)))
        return false;

    return true;
  }

  public SqlQueryResult(ResultSet resultSet) throws SQLException {
    colNames = extractColNames(resultSet.getMetaData());

    rows = new LinkedList<>();
    while(resultSet.next()) {
      Map<String, SqlCell> cells = new HashMap<>();

      for(String colName: colNames)
        cells.put(colName, new SqlCell(resultSet.getString(colName)));

      rows.add(new SqlRow(cells));
    }

  }

  public SqlQueryResult(ResultSet resultSet, String theTableName) throws SQLException {
    this(resultSet);
    tableName = theTableName;
  }

  public List<String> extractColNames(ResultSetMetaData metaData) throws SQLException {
    List<String> colNames = new LinkedList<>();

    for(int i = 1; i <= metaData.getColumnCount(); i++)
      colNames.add(metaData.getColumnName(i));

    return colNames;
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

}