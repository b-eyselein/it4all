package model.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.matching.StringEqualsMatcher;
import play.Logger;

public class SqlQueryResult {

  private static final StringEqualsMatcher COLUMN_NAME_MATCHER = new StringEqualsMatcher("Spaltennamen");

  private final List<String> colNames;

  private final List<SqlRow> rows = new LinkedList<>();

  private String tableName = "";

  // FIXME: In Scala ==>
  // def results[T](resultSet: ResultSet)(f: ResultSet => T) = {
  // new Iterator[T] {
  // def hasNext = resultSet.next()
  // def next() = f(resultSet)
  // }
  // }

  public SqlQueryResult(ResultSet resultSet) throws SQLException {
    final ResultSetMetaData metaData = resultSet.getMetaData();

    colNames = IntStream.range(1, metaData.getColumnCount() + 1).mapToObj(value -> getColName(metaData, value))
        .collect(Collectors.toList());

    while(resultSet.next())
      rows.add(new SqlRow(colNames, resultSet));
  }

  public SqlQueryResult(ResultSet resultSet, String theTableName) throws SQLException {
    this(resultSet);
    tableName = theTableName;
  }

  private static boolean cellsDifferent(SqlCell firstCell, SqlCell secondCell) {
    if(firstCell == null && secondCell == null)
      return false;

    if(firstCell == null ^ secondCell == null)
      return true;

    if(firstCell.equals(secondCell))
      return true;

    firstCell.markAsDifferent();
    secondCell.markAsDifferent();
    return false;
  }

  private static boolean checkCells(List<String> columnNames, SqlRow firstRow, SqlRow secondRow) {
    return columnNames.stream()
        .anyMatch(colName -> cellsDifferent(firstRow.getSqlCell(colName), secondRow.getSqlCell(colName)));
  }

  private static boolean checkColumnNames(List<String> firstColumnsNames, List<String> secondColumnNames) {
    return COLUMN_NAME_MATCHER.match(firstColumnsNames, secondColumnNames).isSuccessful();
  }

  private static boolean checkRows(SqlQueryResult first, SqlQueryResult second, List<String> columnNames) {
    final Iterator<SqlRow> firstRows = first.getRows().iterator();
    final Iterator<SqlRow> secondRows = second.getRows().iterator();

    while(firstRows.hasNext() && secondRows.hasNext()) {
      final SqlRow firstRow = firstRows.next();
      final SqlRow secondRow = secondRows.next();

      if(firstRow.size() != secondRow.size() || !checkCells(columnNames, firstRow, secondRow))
        return false;
    }
    return true;
  }

  private static String getColName(ResultSetMetaData metaData, int value) {
    try {
      return metaData.getColumnLabel(value);
    } catch (final SQLException e) {
      Logger.error("There has been an SQL error:", e);
      return "";
    }
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
    return checkColumnNames(getColumnNames(), other.getColumnNames()) && checkRows(this, other, colNames);
  }

}