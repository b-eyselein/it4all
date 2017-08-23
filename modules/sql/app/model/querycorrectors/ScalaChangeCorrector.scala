package model.querycorrectors

import java.sql.Connection
import model.ScalaUtils.cleanly
import model.StringConsts
import model.exercise.SqlExercise
import model.sql.SqlQueryResult
import play.db.Database
import scala.collection.JavaConverters._

abstract class ScalaChangeCorrector(queryType: String) extends ScalaQueryCorrector(queryType) {

  def runUpdate(connection: Connection, query: String) =
    cleanly(connection.createStatement)(_.close)(statement => statement.executeUpdate(query))

  def runValidationQuery(connection: Connection, query: String) =
    cleanly(connection.createStatement())(_.close)(s => new SqlQueryResult(s.executeQuery(query)))

  def getResultSet(statement: Q, connection: Connection, validation: String) = {
    runUpdate(connection, statement.toString())
    val result = runValidationQuery(connection, validation)
    connection.rollback()
    result
  }

  override def executeQuery(db: Database, userQ: Q, sampleQ: Q, exercise: SqlExercise) =
    cleanly(db.getConnection)(_.close)(connection => {
      connection.setCatalog(exercise.scenario.getShortName())
      connection.setAutoCommit(false)

      val validation = StringConsts.SELECT_ALL_DUMMY + getTableNames(sampleQ)(0)

      val userResult = getResultSet(userQ, connection, validation)

      val sampleResult = getResultSet(sampleQ, connection, validation)

      new SqlExecutionResult(userResult.get, sampleResult.get)
    })

  override def makeOtherComparisons(userQ: Q, sampleQ: Q) = List.empty

}

object ScalaInsertCorrector extends ScalaChangeCorrector("INSERT") {

  type Q = net.sf.jsqlparser.statement.insert.Insert

  override def getColumnWrappers(query: Q) = List.empty

  override def getTableNames(query: Q) = List(query.getTable.getName)

  override def getTables(query: Q) = List(query.getTable)

  override def getWhere(query: Q) = null
}

object ScalaDeleteCorrector extends ScalaChangeCorrector("DELETE") {

  type Q = net.sf.jsqlparser.statement.delete.Delete

  override def getColumnWrappers(query: Q) = List.empty

  override def getTableNames(query: Q) = List(query.getTable.getName)

  override def getTables(query: Q) = query.getTables.asScala.toList

  override def getWhere(query: Q) = query.getWhere()
}

object ScalaUpdateCorrector extends ScalaChangeCorrector("UPDATE") {

  type Q = net.sf.jsqlparser.statement.update.Update

  override def getColumnWrappers(query: Q) = query.getColumns.asScala.map(ScalaColumnWrapper.wrap(_)).toList

  override def getTableNames(query: Q) = query.getTables.asScala.map(_.getName).toList

  override def getTables(query: Q) = query.getTables().asScala.toList

  override def getWhere(query: Q) = query.getWhere()

}
