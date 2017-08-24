package model.querycorrectors

import java.sql.Connection
import model.ScalaUtils.cleanly
import model.StringConsts
import model.exercise.SqlExercise
import model.sql.SqlQueryResult
import play.db.Database
import scala.collection.JavaConverters._

abstract class ChangeCorrector(queryType: String) extends QueryCorrector(queryType) {

  def runUpdate(conn: Connection, query: String) = cleanly(conn.createStatement)(_.close)(s => s.executeUpdate(query))

  def runValidationQuery(conn: Connection, query: String) = cleanly(conn.createStatement)(_.close)(s => new SqlQueryResult(s.executeQuery(query)))

  def getResultSet(statement: Q, connection: Connection, validation: String) = {
    runUpdate(connection, statement.toString)
    val result = runValidationQuery(connection, validation)
    connection.rollback
    result
  }

  override def executeQuery(db: Database, userQ: Q, sampleQ: Q, exercise: SqlExercise) =
    cleanly(db.getConnection)(_.close)(connection => {
      connection.setCatalog(exercise.scenario.getShortName)
      connection.setAutoCommit(false)

      val validation = StringConsts.SELECT_ALL_DUMMY + getTableNames(sampleQ)(0)

      new SqlExecutionResult(getResultSet(userQ, connection, validation).get, getResultSet(sampleQ, connection, validation).get)
    })

  override def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q) = None

  override def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q) = None
}

object InsertCorrector extends ChangeCorrector("INSERT") {

  type Q = net.sf.jsqlparser.statement.insert.Insert

  override def getColumnWrappers(query: Q) = List.empty

  override def getTableNames(query: Q) = List(query.getTable.getName)

  override def getTables(query: Q) = List(query.getTable)

  override def getWhere(query: Q) = null
}

object DeleteCorrector extends ChangeCorrector("DELETE") {

  type Q = net.sf.jsqlparser.statement.delete.Delete

  override def getColumnWrappers(query: Q) = List.empty

  override def getTableNames(query: Q) = List(query.getTable.getName)

  override def getTables(query: Q) = query.getTables.asScala.toList

  override def getWhere(query: Q) = query.getWhere
}

object UpdateCorrector extends ChangeCorrector("UPDATE") {

  type Q = net.sf.jsqlparser.statement.update.Update

  override def getColumnWrappers(query: Q) = query.getColumns.asScala.map(ColumnWrapper.wrap(_)).toList

  override def getTableNames(query: Q) = query.getTables.asScala.map(_.getName).toList

  override def getTables(query: Q) = query.getTables.asScala.toList

  override def getWhere(query: Q) = query.getWhere

}
