package model.tools.sql

import java.sql.{ResultSet, ResultSetMetaData}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.Try

object SqlQueryResult {

  def apply(resultSet: ResultSet, tableName: String = ""): SqlQueryResult = new SqlQueryResult(resultSet, tableName)

}


final case class SqlCell(colName: String, content: String) {

  // FIXME: remove!
  var different: Boolean = false

}

object SqlRow {

  def buildFrom(resultSet: ResultSet): (Seq[String], Seq[SqlRow]) = {
    val metaData: ResultSetMetaData = resultSet.getMetaData

    val columnNames: Seq[String] = (1 to metaData.getColumnCount) map (count => Try(metaData getColumnLabel count) getOrElse "")

    val pRows: ListBuffer[SqlRow] = ListBuffer.empty

    while (resultSet.next) {
      val cells: Map[String, SqlCell] = columnNames map { colName =>
        (colName, SqlCell(colName, Try(resultSet getString colName) getOrElse ""))
      } toMap

      pRows += new SqlRow(cells)
    }

    (columnNames, pRows.toList)
  }

}

final case class SqlRow(cells: Map[String, SqlCell]) {

  def columnNames: Seq[String] = cells.keys.toSeq

  def getCells(columnNames: Seq[String]): Seq[SqlCell] = columnNames flatMap cells.get

  val size: Int = cells.size

  def apply(colName: String): Option[SqlCell] = cells get colName

}

final case class SqlQueryResult(resultSet: ResultSet, tableName: String = "") extends Iterable[SqlRow] {

  val (columnNames, rows): (Seq[String], Seq[SqlRow]) = SqlRow.buildFrom(resultSet)

  override def iterator: Iterator[SqlRow] = rows.iterator

  def isIdentic(that: SqlQueryResult): Boolean = checkRows(this, that, (this.columnNames ++ that.columnNames).distinct)

  private def checkRows(userRes: SqlQueryResult, sampleRes: SqlQueryResult, allColNames: Seq[String]): Boolean = {
    var correct = true

    val zippedRows: Iterable[(SqlRow, SqlRow)] = userRes.zipAll(sampleRes, SqlRow(Map.empty), SqlRow(Map.empty))

    for ((userRow, sampleRow) <- zippedRows; colName <- allColNames) {
      (userRow(colName), sampleRow(colName)) match {

        case (None, None)             => // Do nothing: one query result has more rows than the other!
        case (None, Some(sampleCell)) => sampleCell.different = true
        case (Some(userCell), None)   => userCell.different = true

        case (Some(userCell), Some(sampleCell)) =>
          val cellCorrect = userCell.content == sampleCell.content
          userCell.different = !cellCorrect
          correct &= cellCorrect
      }
    }

    correct
  }


}
