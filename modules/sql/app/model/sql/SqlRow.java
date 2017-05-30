package model.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlRow {

  private Map<String, SqlCell> cells = new HashMap<>();

  public SqlRow(Map<String, SqlCell> theCells) {
    cells = theCells;
  }

  // public void addCell(String columnName, SqlCell cell) {
  // cells.put(columnName, cell);
  // }

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