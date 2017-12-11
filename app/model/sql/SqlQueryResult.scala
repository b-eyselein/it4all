package model.sql

import java.sql.{ResultSet, ResultSetMetaData}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.Try

object SqlQueryResult {

  def apply(resultSet: ResultSet, tableName: String = "") = new SqlQueryResult(resultSet, tableName)

}


class SqlCell(colName: String, resultSet: ResultSet) {

  val content: String = Try(resultSet getString colName) getOrElse ""

  var different: Boolean = false

}

class SqlRow(colNames: Seq[String], resultSet: ResultSet) {

  val cells: Map[String, SqlCell] = colNames map (c => (c, new SqlCell(c, resultSet))) toMap

  def getCells(columnNames: Seq[String]): Seq[SqlCell] = columnNames flatMap cells.get

  val size: Int = cells.size

  def apply(colName: String): Option[SqlCell] = cells get colName

}

case class SqlQueryResult(resultSet: ResultSet, tableName: String = "") extends Iterable[SqlRow] {
  val metaData: ResultSetMetaData = resultSet.getMetaData

  val columnNames: Seq[String] = (1 to metaData.getColumnCount) map (count => Try(metaData getColumnLabel count) getOrElse "")

  private var pRows: ListBuffer[SqlRow] = ListBuffer.empty
  while (resultSet.next)
    pRows += new SqlRow(columnNames, resultSet)

  val rows: Seq[SqlRow] = pRows

  override def iterator: Iterator[SqlRow] = rows.iterator

  val columnCount: Int = columnNames.size

  def isIdentic(that: SqlQueryResult): Boolean = checkRows(this, that, columnNames ++ that.columnNames)

  def checkRows(userRes: SqlQueryResult, sampleRes: SqlQueryResult, colNames: Seq[String]): Boolean = {
    var correct = true

    for ((userRow, sampleRow) <- userRes zip sampleRes; colName <- colNames) {
      (userRow(colName), sampleRow(colName)) match {
        case (Some(userCell), Some(sampleCell)) =>
          val cellCorrect = userCell.content == sampleCell.content
          userCell.different = !cellCorrect
          correct &= cellCorrect
        case (None, Some(sampleCell))           =>
          sampleCell.different = true
        case (Some(userCell), None)             =>
          userCell.different = true
        case (None, None)                       =>
      }
    }

    correct
  }

}