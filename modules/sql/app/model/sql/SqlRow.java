package model.sql;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlRow {

  private Map<String, SqlCell> cells = new HashMap<>();

  public SqlRow(List<String> colNames, ResultSet resultSet) {
    cells = colNames.stream().collect(Collectors.toMap(c -> c, c -> new SqlCell(c, resultSet)));
  }

  public List<SqlCell> getCells(List<String> columnNames) {
    return columnNames.stream().map(cells::get).collect(Collectors.toList());
  }

  public SqlCell getSqlCell(String columnName) {
    return cells.get(columnName);
  }

  public int size() {
    return cells.size();
  }
}