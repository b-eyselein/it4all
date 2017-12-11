package model.sql

import java.sql.{ResultSet, ResultSetMetaData}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.Try

object SqlQueryResult {

  def apply(resultSet: ResultSet, tableName: String = "") = new SqlQueryResult(resultSet, tableName)

}


class SqlCell(colName: String, resultSet: ResultSet) {

  val content: String = Try(resultSet.getString(colName)).getOrElse("")

  var different: Boolean = false

  def markAsDifferent(): Unit = different = true

}

class SqlRow(colNames: List[String], resultSet: ResultSet) {

  val cells: Map[String, SqlCell] = colNames map (c => (c, new SqlCell(c, resultSet))) toMap

  def getCells(columnNames: List[String]): List[SqlCell] = columnNames flatMap cells.get

  val size: Int = cells.size

}

class SqlQueryResult(resultSet: ResultSet, val tableName: String = "") {
  val metaData: ResultSetMetaData = resultSet.getMetaData

  val columnNames: List[String] = (1 to metaData.getColumnCount) map (count => Try(metaData.getColumnLabel(count)) getOrElse "") toList

  private var pRows: ListBuffer[SqlRow] = ListBuffer.empty
  while (resultSet.next)
    pRows += new SqlRow(columnNames, resultSet)


  val rows: List[SqlRow] = pRows toList

  val columnCount: Int = columnNames.size

  def isIdentic(that: SqlQueryResult): Boolean = false // checkColumnNames(columnNames, that.columnNames) && checkRows(this, other, colNames)

  //  private static boolean cellsDifferent(SqlCell firstCell, SqlCell secondCell) {
  //    if (firstCell == null && secondCell == null)
  //      return false
  //
  //    if (firstCell == null ^ secondCell == null)
  //      return true
  //
  //    if (firstCell.equals(secondCell))
  //      return true
  //
  //    firstCell.markAsDifferent()
  //    secondCell.markAsDifferent()
  //    return false
  //  }

  //  private static boolean checkCells(List < String > columnNames, SqlRow firstRow, SqlRow secondRow) {
  //    return columnNames.stream()
  //      .anyMatch(colName -> cellsDifferent(firstRow.getSqlCell(colName), secondRow.getSqlCell(colName)))
  //  }

  //  private static boolean checkColumnNames(List < String > firstColumnsNames, List < String > secondColumnNames) {
  //    return COLUMN_NAME_MATCHER.doJavaMatch(firstColumnsNames, secondColumnNames).isSuccessful()
  //  }

  //  private static boolean checkRows(SqlQueryResult first, SqlQueryResult second, List < String > columnNames) {
  //    final Iterator < SqlRow > firstRows = first.getRows().iterator()
  //    final Iterator < SqlRow > secondRows = second.getRows().iterator()
  //
  //    while (firstRows.hasNext() && secondRows.hasNext()) {
  //      final SqlRow firstRow = firstRows.next()
  //      final SqlRow secondRow = secondRows.next()
  //
  //      if (firstRow.size() != secondRow.size() || !checkCells(columnNames, firstRow, secondRow))
  //        return false
  //    }
  //    return true
  //  }

}