package model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlQueryResult {

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
    // FIXME: implement comparison: return (new?) subclass of EvaluationResult!

    // Check columnNames with order
    int columnCount = colNames.size();
    if(other.getColumnCount() != columnCount)
      return false;
    for(int i = 0; i < columnCount; i++)
      if(!other.getColumnNames().get(i).toLowerCase().equals(colNames.get(i).toLowerCase()))
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