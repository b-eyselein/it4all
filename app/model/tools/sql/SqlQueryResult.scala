package model.tools.sql

import java.sql.{ResultSet, ResultSetMetaData}

import scala.util.Try

final case class SqlCell(colName: String, content: Option[String], different: Boolean = false)

final case class SqlRow(cells: Map[String, SqlCell])

object SqlQueryResult {

  def fromResultSet(resultSet: ResultSet, tableName: String = ""): SqlQueryResult = {

    @annotation.tailrec
    def go(resultSet: ResultSet, columnNames: Seq[String], acc: Seq[SqlRow]): Seq[SqlRow] =
      if (!resultSet.next()) acc
      else {
        val cells: Map[String, SqlCell] = columnNames.map { colName =>
          val content = Option(
            Try(resultSet.getString(colName)).getOrElse("")
          )

          (colName, SqlCell(colName, content))
        }.toMap

        go(resultSet, columnNames, acc :+ SqlRow(cells))
      }

    val metaData: ResultSetMetaData = resultSet.getMetaData

    val columnNames: Seq[String] =
      (1 to metaData.getColumnCount).map(count => Try(metaData.getColumnLabel(count)).getOrElse(""))

    SqlQueryResult(columnNames, go(resultSet, columnNames, Seq.empty), tableName)
  }

  def compareAndUpdateUserQueryResult(userResult: SqlQueryResult, sampleResult: SqlQueryResult): SqlQueryResult = {
    // FIXME: implement!

    @annotation.tailrec
    def go(rows: List[(SqlRow, SqlRow)], acc: Seq[SqlRow]): SqlQueryResult =
      rows match {
        case Nil => SqlQueryResult(userResult.columnNames, acc, userResult.tableName)
        case (userRow, sampleRow) :: tail =>
          val newUserRow = userRow.cells.map {
            case (userColName, userCell) =>
              val newCell = userCell.copy(
                different = !sampleRow.cells
                  .get(userColName)
                  .exists { _.content == userCell.content }
              )

              (userColName, newCell)
          }

          go(tail, acc :+ SqlRow(newUserRow))
      }

    go((userResult.rows zip sampleResult.rows).toList, Seq.empty)
  }

}

final case class SqlQueryResult(columnNames: Seq[String], rows: Seq[SqlRow], tableName: String = "")
